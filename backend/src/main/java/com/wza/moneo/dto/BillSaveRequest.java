package com.wza.moneo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wza.moneo.common.enums.RecordType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record BillSaveRequest(
        @NotNull(message = "收支类型不能为空") RecordType type,
        @NotNull(message = "分类不能为空") Long categoryId,
        @NotNull(message = "账户不能为空") Long accountId,
        @NotNull(message = "金额不能为空") @DecimalMin(value = "0.01", message = "金额必须大于0") BigDecimal amount,
        @NotNull(message = "账单日期不能为空") @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
        @JsonFormat(pattern = "HH:mm") LocalTime time,
        @Size(max = 255, message = "备注不能超过255个字符") String note
) {
}
