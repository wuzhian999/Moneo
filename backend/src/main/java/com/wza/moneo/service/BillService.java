package com.wza.moneo.service;

import com.wza.moneo.common.result.PageResult;
import com.wza.moneo.dto.BillQuery;
import com.wza.moneo.dto.BillSaveRequest;
import com.wza.moneo.vo.BillVo;

public interface BillService {
    BillVo create(BillSaveRequest request);
    PageResult<BillVo> page(BillQuery query);
    BillVo getById(Long id);
    BillVo update(Long id, BillSaveRequest request);
    void delete(Long id);
}
