package com.wza.moneo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wza.moneo.common.enums.RecordType;
import com.wza.moneo.entity.Bill;
import com.wza.moneo.mapper.BillMapper;
import com.wza.moneo.service.BudgetService;
import com.wza.moneo.service.DashboardService;
import com.wza.moneo.vo.DashboardSummaryVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final BillMapper billMapper;
    private final BudgetService budgetService;

    @Override
    public DashboardSummaryVo summary(YearMonth month) {
        LocalDateTime start = month.atDay(1).atStartOfDay();
        LocalDateTime end = month.plusMonths(1).atDay(1).atStartOfDay();
        List<Bill> monthBills = findBetween(start, end);
        BigDecimal income = sumByType(monthBills, RecordType.INCOME);
        BigDecimal expense = sumByType(monthBills, RecordType.EXPENSE);
        return new DashboardSummaryVo(month.toString(), income, expense, income.subtract(expense), budgetService.getAmount(month));
    }

    private List<Bill> findBetween(LocalDateTime start, LocalDateTime end) {
        return billMapper.selectList(new LambdaQueryWrapper<Bill>()
                .ge(Bill::getOccurredAt, start)
                .lt(Bill::getOccurredAt, end));
    }

    private BigDecimal sumByType(List<Bill> bills, RecordType type) {
        return bills.stream().filter(bill -> bill.getType() == type).map(Bill::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
