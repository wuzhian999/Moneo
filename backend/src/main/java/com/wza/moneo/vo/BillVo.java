package com.wza.moneo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wza.moneo.common.enums.RecordType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BillVo(
        Long id,
        RecordType type,
        Long categoryId,
        Long accountId,
        String category,
        String icon,
        String color,
        BigDecimal amount,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
        String note
) {
}
