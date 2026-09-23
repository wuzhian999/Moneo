package com.wza.moneo.controller;

import com.wza.moneo.common.enums.RecordType;
import com.wza.moneo.common.result.ApiResult;
import com.wza.moneo.dto.CategorySaveRequest;
import com.wza.moneo.service.CategoryService;
import com.wza.moneo.vo.CategoryVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ApiResult<List<CategoryVo>> list(@RequestParam(required = false) RecordType type) {
        return ApiResult.success(categoryService.list(type));
    }

    @GetMapping("/{id}")
    public ApiResult<CategoryVo> getById(@PathVariable Long id) {
        return ApiResult.success(categoryService.getById(id));
    }

    @PostMapping
    public ApiResult<CategoryVo> create(@Valid @RequestBody CategorySaveRequest request) {
        return ApiResult.success(categoryService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResult<CategoryVo> update(@PathVariable Long id, @Valid @RequestBody CategorySaveRequest request) {
        return ApiResult.success(categoryService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ApiResult.success();
    }
}
