package com.wza.moneo.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResult<T> {

    private final int code;
    private final String message;
    private final T data;

    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(0, "success", data);
    }

    public static ApiResult<Void> success() {
        return new ApiResult<>(0, "success", null);
    }

    public static <T> ApiResult<T> failure(int code, String message, T data) {
        return new ApiResult<>(code, message, data);
    }
}
