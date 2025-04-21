package com.jh.education.common.bean;

import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;

@Data
public class CommonResult<T> {
    private Integer code;
    private String message;
    private T data;

    private CommonResult(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> CommonResult success(String message, T data) {
        return new CommonResult<>(HttpServletResponse.SC_OK, message, data);
    }

    public static <T> CommonResult success(String message) {
        return new CommonResult<>(HttpServletResponse.SC_OK, message, null);
    }

    public static <T> CommonResult fail(Integer code, String message) {
        return new CommonResult<>(code, message, null);
    }
}
