package com.jh.education.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginByPasswordDTO {
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3456789]\\d{9}$", message = "手机号格式错误")
    private String phoneNumber;

    @NotBlank(message = "密码不能为空")
    private String password;
}
