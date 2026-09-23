package com.wza.moneo.vo;

import java.math.BigDecimal;

public record DashboardSummaryVo(
        String month,
        BigDecimal income,
        BigDecimal expense,
        BigDecimal balance,
        BigDecimal budget
) {
}
