package com.wza.moneo.controller;

import com.wza.moneo.common.result.ApiResult;
import com.wza.moneo.common.result.PageResult;
import com.wza.moneo.dto.BillQuery;
import com.wza.moneo.dto.BillSaveRequest;
import com.wza.moneo.service.BillService;
import com.wza.moneo.vo.BillVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
@Validated
public class BillController {

    private final BillService billService;

    @PostMapping
    public ApiResult<BillVo> create(@Valid @RequestBody BillSaveRequest request) {
        return ApiResult.success(billService.create(request));
    }

    @GetMapping
    public ApiResult<PageResult<BillVo>> page(@Valid @ModelAttribute BillQuery query) {
        return ApiResult.success(billService.page(query));
    }

    @GetMapping("/{id}")
    public ApiResult<BillVo> getById(@PathVariable Long id) {
        return ApiResult.success(billService.getById(id));
    }

    @PutMapping("/{id}")
    public ApiResult<BillVo> update(@PathVariable Long id, @Valid @RequestBody BillSaveRequest request) {
        return ApiResult.success(billService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) {
        billService.delete(id);
        return ApiResult.success();
    }
}
