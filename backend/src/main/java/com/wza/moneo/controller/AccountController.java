package com.wza.moneo.controller;

import com.wza.moneo.common.result.ApiResult;
import com.wza.moneo.dto.AccountSaveRequest;
import com.wza.moneo.service.AccountService;
import com.wza.moneo.vo.AccountVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public ApiResult<List<AccountVo>> list() {
        return ApiResult.success(accountService.list());
    }

    @GetMapping("/{id}")
    public ApiResult<AccountVo> getById(@PathVariable Long id) {
        return ApiResult.success(accountService.getById(id));
    }

    @PostMapping
    public ApiResult<AccountVo> create(@Valid @RequestBody AccountSaveRequest request) {
        return ApiResult.success(accountService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResult<AccountVo> update(@PathVariable Long id, @Valid @RequestBody AccountSaveRequest request) {
        return ApiResult.success(accountService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) {
        accountService.delete(id);
        return ApiResult.success();
    }
}
