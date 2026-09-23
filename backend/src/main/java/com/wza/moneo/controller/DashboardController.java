package com.wza.moneo.controller;

import com.wza.moneo.common.exception.BusinessException;
import com.wza.moneo.common.result.ApiResult;
import com.wza.moneo.service.DashboardService;
import com.wza.moneo.vo.DashboardSummaryVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public ApiResult<DashboardSummaryVo> summary(@RequestParam(required = false) String month) {
        return ApiResult.success(dashboardService.summary(parseMonth(month)));
    }

    private YearMonth parseMonth(String month) {
        if (month == null || month.isBlank()) {
            return YearMonth.now();
        }
        try {
            return YearMonth.parse(month);
        } catch (RuntimeException exception) {
            throw new BusinessException(400, "month格式必须为yyyy-MM");
        }
    }
}
