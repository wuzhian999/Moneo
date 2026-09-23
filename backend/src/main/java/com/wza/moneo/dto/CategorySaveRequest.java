package com.wza.moneo.dto;

import com.wza.moneo.common.enums.RecordType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CategorySaveRequest(
        @NotBlank(message = "分类名称不能为空") @Size(max = 64, message = "分类名称不能超过64个字符") String name,
        @NotNull(message = "收支类型不能为空") RecordType type,
        @NotBlank(message = "分类图标不能为空") @Size(max = 32, message = "图标不能超过32个字符") String icon,
        @NotBlank(message = "分类颜色不能为空") @Size(max = 16, message = "颜色值不能超过16个字符") String color,
        Integer sortOrder
) {
}
