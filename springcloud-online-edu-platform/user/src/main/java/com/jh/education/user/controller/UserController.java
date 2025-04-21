package com.jh.education.user.controller;

import com.jh.education.common.bean.CommonResult;
import com.jh.education.feign.vo.UserInfo;
import com.jh.education.user.dto.ChangePasswordDTO;
import com.jh.education.user.dto.LoginByPasswordDTO;
import com.jh.education.user.dto.LoginByVerificationCodeDTO;
import com.jh.education.user.dto.RetrievePasswordDTO;
import com.jh.education.user.service.UserService;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author jh
 * @since 2023-07-14
 */
@Slf4j
@RestController
@RequestMapping("/user/user")
public class UserController {
    @Resource
    public UserService userService;

    @PostMapping("/login_by_verification_code")
    @PreAuthorize("isAnonymous()")
    public CommonResult loginByVerificationCode(@Valid @RequestBody LoginByVerificationCodeDTO dto) {
        log.info("用户登录: {}", dto);
        return CommonResult.success("登录成功", userService.loginByVerificationCode(dto));
    }

    @PostMapping("/login_by_password")
    @PreAuthorize("isAnonymous()")
    public CommonResult loginByPassword(@Valid @RequestBody LoginByPasswordDTO dto) {
        log.info("用户登录: {}", dto);
        return CommonResult.success("登录成功", userService.loginByPassword(dto));
    }

    @GetMapping("/verification_code")
    public CommonResult getVerificationCode(String phoneNumber) throws TencentCloudSDKException {
        if (!phoneNumber.matches("1[3456789]\\d{9}")) {
            throw new IllegalArgumentException("手机号格式错误");
        }
        log.info("获取验证码，手机号为 {}", phoneNumber);
        userService.getVerificationCode(phoneNumber);
        return CommonResult.success("获取验证码成功");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isFullyAuthenticated()")
    public CommonResult deleteUser(@PathVariable Long id) {
        log.info("删除ID为 {} 的用户", id);
        return CommonResult.success(userService.removeById(id) ? "删除成功" : "删除失败");
    }

    // 已通过身份验证且未被“记住”的用户发送的请求
    @GetMapping("/logout")
    @PreAuthorize("isFullyAuthenticated()")
    public CommonResult logout() {
        userService.logout();
        return CommonResult.success("退出成功");
    }

    @PostMapping("/change_password")
    @PreAuthorize("isFullyAuthenticated()")
    public CommonResult changePassword(@RequestBody @Valid ChangePasswordDTO dto) {
        userService.changePassword(dto);
        return CommonResult.success("密码修改成功");
    }

    @PostMapping("/retrieve_password")
    @PreAuthorize("isAnonymous()")
    public CommonResult retrievePassword(@RequestBody @Valid RetrievePasswordDTO dto) {
        userService.retrievePassword(dto);
        return CommonResult.success("找回密码成功");
    }

    @GetMapping("/userInfo/{id}")
    public UserInfo getUserInfo(@PathVariable Long id) {
        return userService.getUserInfo(id);
    }
}
