package com.wza.moneo.service;

import com.wza.moneo.vo.DashboardSummaryVo;

import java.time.YearMonth;

public interface DashboardService {
    DashboardSummaryVo summary(YearMonth month);
}
