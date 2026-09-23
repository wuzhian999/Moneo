package com.wza.moneo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record BudgetSaveRequest(
        @NotNull(message = "预算金额不能为空")
        @DecimalMin(value = "0.01", message = "预算金额必须大于0")
        BigDecimal amount
) {
}
