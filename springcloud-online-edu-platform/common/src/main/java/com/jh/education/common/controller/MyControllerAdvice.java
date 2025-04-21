package com.jh.education.common.controller;

import com.jh.education.common.bean.CommonResult;
import com.jh.education.common.exception.MessagePromptException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

import java.util.List;

@RestControllerAdvice
public class MyControllerAdvice {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResult handleMessagePromptException(MessagePromptException e) {
        return CommonResult.success(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResult exceptionHandle(Exception e) {
        return CommonResult.fail(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
    }

    @ExceptionHandler({IllegalArgumentException.class, MultipartException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResult exceptionHandle1(Exception e) {
        return CommonResult.fail(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler({BadCredentialsException.class, AuthenticationException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public CommonResult exceptionHandle2(Exception e) {
        return CommonResult.fail(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public CommonResult handleAccessDeniedException(AccessDeniedException e) {
        return CommonResult.fail(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResult handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        StringBuilder sb = new StringBuilder();
        for (FieldError fieldError : fieldErrors) {
            if (fieldError.getField().endsWith("Time")) {
                sb.append("日期时间格式错误，正确格式为：yyyy-MM-dd HH:mm:ss；");
            } else {
                sb.append(fieldError.getDefaultMessage()).append("；");
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        return CommonResult.fail(HttpServletResponse.SC_BAD_REQUEST, sb.toString());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResult handleDuplicateKeyException(DuplicateKeyException e) {
        String message = e.getRootCause().getMessage();
        int startIndex = message.lastIndexOf(".") + 1;
        int endIndex = message.lastIndexOf("_uindex'");
        String s = message.substring(startIndex, endIndex);
        return CommonResult.fail(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, s.substring(s.lastIndexOf('_') + 1) + "不能重复");
    }
}