package com.jh.education.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class TeacherRegisterDTO {
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3456789]\\d{9}$", message = "手机号格式错误")
    private String phoneNumber;
    // 验证码
    @NotBlank(message = "验证码不能为空")
    @Pattern(regexp = "^\\d{6}$", message = "验证码格式错误")
    private String verificationCode;
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    private String password;
    @NotNull(message = "性别不能为空")
    private Boolean gender;
    private MultipartFile headPortrait;
    @NotBlank(message = "个人简介不能为空")
    private String introduction;
    @NotBlank(message = "真名不能为空")
    private String realName;
    @NotNull(message = "教资证不能为空")
    private MultipartFile teacherCertificateImg;
}