package com.wza.moneo.service;

import com.wza.moneo.dto.BudgetSaveRequest;
import com.wza.moneo.vo.BudgetVo;

import java.math.BigDecimal;
import java.time.YearMonth;

public interface BudgetService {
    BudgetVo get(YearMonth month);
    BudgetVo save(YearMonth month, BudgetSaveRequest request);
    BigDecimal getAmount(YearMonth month);
}
