package com.wza.moneo.dto;

import com.wza.moneo.common.enums.RecordType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class BillQuery {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;
    private RecordType type;
    private Long categoryId;
    private Long accountId;
    private String keyword;
    @Min(value = 1, message = "page必须大于0")
    private long page = 1;
    @Min(value = 1, message = "pageSize必须大于0")
    @Max(value = 100, message = "pageSize不能超过100")
    private long pageSize = 20;
}
