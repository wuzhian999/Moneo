package com.wza.moneo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wza.moneo.dto.BudgetSaveRequest;
import com.wza.moneo.entity.Budget;
import com.wza.moneo.mapper.BudgetMapper;
import com.wza.moneo.service.BudgetService;
import com.wza.moneo.vo.BudgetVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.YearMonth;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private final BudgetMapper budgetMapper;

    @Override
    public BudgetVo get(YearMonth month) {
        return new BudgetVo(month.toString(), getAmount(month));
    }

    @Override
    @Transactional
    public BudgetVo save(YearMonth month, BudgetSaveRequest request) {
        Budget budget = find(month);
        if (budget == null) {
            budget = new Budget();
            budget.setBudgetMonth(month.atDay(1));
            budget.setAmount(request.amount());
            budgetMapper.insert(budget);
        } else {
            budget.setAmount(request.amount());
            budgetMapper.updateById(budget);
        }
        return new BudgetVo(month.toString(), budget.getAmount());
    }

    @Override
    public BigDecimal getAmount(YearMonth month) {
        Budget budget = find(month);
        return budget == null ? BigDecimal.ZERO : budget.getAmount();
    }

    private Budget find(YearMonth month) {
        return budgetMapper.selectOne(new LambdaQueryWrapper<Budget>()
                .eq(Budget::getBudgetMonth, month.atDay(1)));
    }
}
