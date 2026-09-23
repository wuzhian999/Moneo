package com.wza.moneo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record AccountSaveRequest(
        @NotBlank(message = "账户名称不能为空") @Size(max = 64, message = "账户名称不能超过64个字符") String name,
        @Size(max = 32, message = "图标不能超过32个字符") String icon,
        @Size(max = 16, message = "颜色值不能超过16个字符") String color,
        @DecimalMin(value = "0.00", message = "初始余额不能小于0") BigDecimal initialBalance,
        Boolean isDefault
) {
}
