package com.wza.moneo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wza.moneo.common.exception.BusinessException;
import com.wza.moneo.common.exception.ResourceNotFoundException;
import com.wza.moneo.common.result.PageResult;
import com.wza.moneo.dto.BillQuery;
import com.wza.moneo.dto.BillSaveRequest;
import com.wza.moneo.entity.Bill;
import com.wza.moneo.entity.Category;
import com.wza.moneo.mapper.BillMapper;
import com.wza.moneo.service.AccountService;
import com.wza.moneo.service.BillService;
import com.wza.moneo.service.CategoryService;
import com.wza.moneo.vo.BillVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BillServiceImpl implements BillService {

    private final BillMapper billMapper;
    private final CategoryService categoryService;
    private final AccountService accountService;

    @Override
    public BillVo create(BillSaveRequest request) {
        Bill bill = new Bill();
        apply(bill, request);
        billMapper.insert(bill);
        return toVo(bill, categoryService.getRequired(bill.getCategoryId()));
    }

    @Override
    public PageResult<BillVo> page(BillQuery query) {
        if (query.getStartDate() != null && query.getEndDate() != null && query.getStartDate().isAfter(query.getEndDate())) {
            throw new BusinessException(400, "startDate不能晚于endDate");
        }
        LambdaQueryWrapper<Bill> wrapper = buildQuery(query)
                .orderByDesc(Bill::getOccurredAt)
                .orderByDesc(Bill::getId);
        Page<Bill> result = billMapper.selectPage(Page.of(query.getPage(), query.getPageSize()), wrapper);
        Map<Long, Category> categories = result.getRecords().stream()
                .map(Bill::getCategoryId)
                .distinct()
                .map(categoryService::getRequired)
                .collect(Collectors.toMap(Category::getId, Function.identity()));
        return new PageResult<>(result.getRecords().stream().map(bill -> toVo(bill, categories.get(bill.getCategoryId()))).toList(),
                result.getCurrent(), result.getSize(), result.getTotal());
    }

    @Override
    public BillVo getById(Long id) {
        Bill bill = getRequired(id);
        return toVo(bill, categoryService.getRequired(bill.getCategoryId()));
    }

    @Override
    public BillVo update(Long id, BillSaveRequest request) {
        Bill bill = getRequired(id);
        apply(bill, request);
        billMapper.updateById(bill);
        return toVo(bill, categoryService.getRequired(bill.getCategoryId()));
    }

    @Override
    public void delete(Long id) {
        billMapper.deleteById(getRequired(id).getId());
    }

    private Bill getRequired(Long id) {
        Bill bill = billMapper.selectById(id);
        if (bill == null) {
            throw new ResourceNotFoundException("账单");
        }
        return bill;
    }

    private LambdaQueryWrapper<Bill> buildQuery(BillQuery query) {
        LocalDateTime endExclusive = query.getEndDate() == null ? null : query.getEndDate().plusDays(1).atStartOfDay();
        return new LambdaQueryWrapper<Bill>()
                .ge(query.getStartDate() != null, Bill::getOccurredAt, query.getStartDate() == null ? null : query.getStartDate().atStartOfDay())
                .lt(endExclusive != null, Bill::getOccurredAt, endExclusive)
                .eq(query.getType() != null, Bill::getType, query.getType())
                .eq(query.getCategoryId() != null, Bill::getCategoryId, query.getCategoryId())
                .eq(query.getAccountId() != null, Bill::getAccountId, query.getAccountId())
                .like(query.getKeyword() != null && !query.getKeyword().isBlank(), Bill::getNote, query.getKeyword());
    }

    private void apply(Bill bill, BillSaveRequest request) {
        Category category = categoryService.getRequired(request.categoryId());
        accountService.getRequired(request.accountId());
        if (category.getType() != request.type()) {
            throw new BusinessException(400, "账单类型必须与分类类型一致");
        }
        bill.setType(request.type());
        bill.setCategoryId(request.categoryId());
        bill.setAccountId(request.accountId());
        bill.setAmount(request.amount());
        bill.setOccurredAt(LocalDateTime.of(request.date(), request.time() == null ? LocalTime.now().withSecond(0).withNano(0) : request.time()));
        bill.setNote(request.note() == null || request.note().isBlank() ? null : request.note().trim());
    }

    private BillVo toVo(Bill bill, Category category) {
        return new BillVo(bill.getId(), bill.getType(), bill.getCategoryId(), bill.getAccountId(), category.getName(), category.getIcon(), category.getColor(),
                bill.getAmount(), bill.getOccurredAt().toLocalDate(), bill.getOccurredAt().toLocalTime(), bill.getNote());
    }
}
