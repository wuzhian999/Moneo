package com.wza.moneo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wza.moneo.common.enums.RecordType;
import com.wza.moneo.common.exception.BusinessException;
import com.wza.moneo.common.exception.ResourceNotFoundException;
import com.wza.moneo.dto.CategorySaveRequest;
import com.wza.moneo.entity.Category;
import com.wza.moneo.entity.Bill;
import com.wza.moneo.mapper.BillMapper;
import com.wza.moneo.mapper.CategoryMapper;
import com.wza.moneo.service.CategoryService;
import com.wza.moneo.vo.CategoryVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final BillMapper billMapper;

    @Override
    public List<CategoryVo> list(RecordType type) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<Category>()
                .eq(type != null, Category::getType, type)
                .orderByAsc(Category::getSortOrder)
                .orderByAsc(Category::getId);
        return categoryMapper.selectList(wrapper).stream().map(this::toVo).toList();
    }

    @Override
    public CategoryVo getById(Long id) {
        return toVo(getRequired(id));
    }

    @Override
    public CategoryVo create(CategorySaveRequest request) {
        ensureNameAvailable(request.name(), request.type(), null);
        Category category = new Category();
        apply(category, request);
        categoryMapper.insert(category);
        return toVo(category);
    }

    @Override
    public CategoryVo update(Long id, CategorySaveRequest request) {
        Category category = getRequired(id);
        ensureNameAvailable(request.name(), request.type(), id);
        apply(category, request);
        categoryMapper.updateById(category);
        return toVo(category);
    }

    @Override
    public void delete(Long id) {
        Category category = getRequired(id);
        Long billCount = billMapper.selectCount(new LambdaQueryWrapper<Bill>().eq(Bill::getCategoryId, id));
        if (billCount != null && billCount > 0) {
            throw new BusinessException(409, "已有账单使用该分类，无法删除");
        }
        categoryMapper.deleteById(category.getId());
    }

    @Override
    public Category getRequired(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new ResourceNotFoundException("分类");
        }
        return category;
    }

    private void ensureNameAvailable(String name, RecordType type, Long excludeId) {
        Long count = categoryMapper.selectCount(new LambdaQueryWrapper<Category>()
                .eq(Category::getName, name)
                .eq(Category::getType, type)
                .ne(excludeId != null, Category::getId, excludeId));
        if (count != null && count > 0) {
            throw new BusinessException(409, "同类型分类名称已存在");
        }
    }

    private void apply(Category category, CategorySaveRequest request) {
        category.setName(request.name());
        category.setType(request.type());
        category.setIcon(request.icon());
        category.setColor(request.color());
        category.setSortOrder(request.sortOrder() == null ? 0 : request.sortOrder());
    }

    private CategoryVo toVo(Category category) {
        return new CategoryVo(category.getId(), category.getName(), category.getType(), category.getIcon(), category.getColor(), category.getSortOrder());
    }
}
