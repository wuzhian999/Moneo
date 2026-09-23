package com.wza.moneo.service;

import com.wza.moneo.common.enums.RecordType;
import com.wza.moneo.dto.CategorySaveRequest;
import com.wza.moneo.entity.Category;
import com.wza.moneo.vo.CategoryVo;

import java.util.List;

public interface CategoryService {
    List<CategoryVo> list(RecordType type);
    CategoryVo getById(Long id);
    CategoryVo create(CategorySaveRequest request);
    CategoryVo update(Long id, CategorySaveRequest request);
    void delete(Long id);
    Category getRequired(Long id);
}
