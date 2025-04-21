package com.jh.education.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jh.education.feign.vo.UserInfo;
import com.jh.education.user.dto.ChangePasswordDTO;
import com.jh.education.user.dto.LoginByPasswordDTO;
import com.jh.education.user.dto.LoginByVerificationCodeDTO;
import com.jh.education.user.dto.RetrievePasswordDTO;
import com.jh.education.user.entity.User;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;

import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author jh
 * @since 2023-07-14
 */
public interface UserService extends IService<User> {
    // 获取验证码
    void getVerificationCode(String phoneNumber) throws TencentCloudSDKException;

    Map<String, Object> loginByVerificationCode(LoginByVerificationCodeDTO dto);

    void logout();

    Map<String, Object> loginByPassword(LoginByPasswordDTO dto);

    UserInfo getUserInfo(Long id);

    boolean changePassword(ChangePasswordDTO dto);

    boolean retrievePassword(RetrievePasswordDTO dto);
}