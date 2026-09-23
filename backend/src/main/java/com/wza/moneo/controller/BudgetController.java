package com.wza.moneo.controller;

import com.wza.moneo.common.exception.BusinessException;
import com.wza.moneo.common.result.ApiResult;
import com.wza.moneo.dto.BudgetSaveRequest;
import com.wza.moneo.service.BudgetService;
import com.wza.moneo.vo.BudgetVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @GetMapping("/{month}")
    public ApiResult<BudgetVo> get(@PathVariable String month) {
        return ApiResult.success(budgetService.get(parseMonth(month)));
    }

    @PutMapping("/{month}")
    public ApiResult<BudgetVo> save(@PathVariable String month, @Valid @RequestBody BudgetSaveRequest request) {
        return ApiResult.success(budgetService.save(parseMonth(month), request));
    }

    private YearMonth parseMonth(String month) {
        try {
            return YearMonth.parse(month);
        } catch (DateTimeParseException exception) {
            throw new BusinessException(400, "月份格式必须为 yyyy-MM");
        }
    }
}
