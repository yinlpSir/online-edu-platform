package com.jh.education.user.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.jwt.JWTUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jh.education.common.bean.LoginUser;
import com.jh.education.common.config.PhoneNumberAuthenticationToken;
import com.jh.education.common.constant.UserRole;
import com.jh.education.common.exception.MessagePromptException;
import com.jh.education.common.util.COSUtil;
import com.jh.education.common.util.UserUtil;
import com.jh.education.feign.vo.UserInfo;
import com.jh.education.user.constant.DefaultValue;
import com.jh.education.user.constant.VerificationCodeConstant;
import com.jh.education.user.dto.ChangePasswordDTO;
import com.jh.education.user.dto.LoginByPasswordDTO;
import com.jh.education.user.dto.LoginByVerificationCodeDTO;
import com.jh.education.user.dto.RetrievePasswordDTO;
import com.jh.education.user.entity.User;
import com.jh.education.user.mapper.UserMapper;
import com.jh.education.user.properties.SmsProperties;
import com.jh.education.user.properties.TencentProperties;
import com.jh.education.user.service.StudentService;
import com.jh.education.user.service.TeacherService;
import com.jh.education.user.service.UserService;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author jh
 * @since 2023-07-14
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Value("${JWTSigner}")
    private String JWTSigner;

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private StudentService studentService;

    @Resource
    private TeacherService teacherService;

    @Resource
    private TencentProperties tencentProperties;

    @Resource
    private SmsProperties smsProperties;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public void getVerificationCode(String phoneNumber) throws TencentCloudSDKException {
        // 已有验证码
        if (redisTemplate.opsForValue().get(phoneNumber) != null) {
            throw new BadCredentialsException("已有验证码，请勿重复获取！");
        }

        // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
        // 代码泄露可能会导致 SecretId 和 SecretKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议采用更安全的方式来使用密钥，请参见：https://cloud.tencent.com/document/product/1278/85305
        // 密钥可前往官网控制台 https://console.cloud.tencent.com/cam/capi 进行获取
        Credential cred = new Credential(tencentProperties.getSecretId(), tencentProperties.getSecretKey());
        // 实例化要请求产品的client对象
        SmsClient client = new SmsClient(cred, smsProperties.getRegion());
        // 实例化一个请求对象,每个接口都会对应一个request对象
        SendSmsRequest req = new SendSmsRequest();
        String[] phoneNumberSet1 = {phoneNumber};
        req.setPhoneNumberSet(phoneNumberSet1);
        req.setSmsSdkAppId(smsProperties.getSdkAppId());
        req.setSignName(smsProperties.getSignName());
        req.setTemplateId(VerificationCodeConstant.SMS_TEMPLATE_ID);

        // 生成验证码
        String verificationCode = RandomUtil.randomNumbers(VerificationCodeConstant.LENGTH);

        // 存储到Redis中
        redisTemplate.opsForValue().set(phoneNumber, verificationCode, VerificationCodeConstant.VALID_TIME, TimeUnit.MINUTES);

        String[] templateParamSet1 = {verificationCode};
        req.setTemplateParamSet(templateParamSet1);

        // 返回的resp是一个SendSmsResponse的实例，与请求对象对应
        SendSmsResponse resp = client.SendSms(req);
        // 输出json格式的字符串回包
        log.info(SendSmsResponse.toJsonString(resp));
    }

    @Override
    public Map<String, Object> loginByVerificationCode(LoginByVerificationCodeDTO dto) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhoneNumber, dto.getPhoneNumber());
        User user = getOne(wrapper);
        if (user == null) {
            throw new MessagePromptException("该用户不存在！");
        }
        PhoneNumberAuthenticationToken authToken = new PhoneNumberAuthenticationToken(dto.getPhoneNumber(), dto.getVerificationCode());
        return loginAuthentication(authToken);
    }

    @Override
    public Map<String, Object> loginByPassword(LoginByPasswordDTO dto) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhoneNumber, dto.getPhoneNumber());
        User user = getOne(wrapper);
        if (user == null) {
            throw new MessagePromptException("该用户不存在！");
        }
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(dto.getPhoneNumber(), dto.getPassword());
        return loginAuthentication(authToken);
    }

    @Override
    public UserInfo getUserInfo(Long id) {
        User user = getById(id);
        if (user == null) {
            throw new MessagePromptException("该用户不存在！");
        }
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(user.getUsername());
        userInfo.setHeadPortrait(COSUtil.getFileUrl(user.getHeadPortrait()));
        return userInfo;
    }

    @Override
    public boolean changePassword(ChangePasswordDTO dto) {
        LoginUser nowUser = UserUtil.getNowUser();
        if (!Objects.equals(redisTemplate.opsForValue().get(nowUser.getPhoneNumber()), dto.getConfirmPassword())) {
            throw new MessagePromptException("验证码错误！");
        }
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new MessagePromptException("密码和确认密码不一致");
        }
        User user = new User();
        user.setId(nowUser.getId());
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        return updateById(user);
    }

    @Override
    public boolean retrievePassword(RetrievePasswordDTO dto) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhoneNumber, dto.getPhoneNumber());
        User user = getOne(wrapper);
        if (user == null) {
            throw new MessagePromptException("该用户不存在！");
        }
        if (!dto.getConfirmPassword().equals(redisTemplate.opsForValue().get(dto.getPhoneNumber()))) {
            throw new MessagePromptException("验证码错误！");
        }
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new MessagePromptException("密码和确认密码不一致");
        }
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        return updateById(user);
    }

    private Map<String, Object> loginAuthentication(AbstractAuthenticationToken authToken) {
        Authentication authenticate = authenticationManager.authenticate(authToken);
        if (authenticate == null) {
            throw new MessagePromptException("登录失败");
        }

        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();

        // 将当前用户缓存到 Redis 中
        redisTemplate.opsForValue().set(loginUser.getId(), loginUser, 30, TimeUnit.DAYS);

        Map<String, Object> payload = new HashMap<>(2);
        payload.put("userId", loginUser.getId());
        payload.put("exp", LocalDateTime.now().plusMonths(1));
        String token = JWTUtil.createToken(payload, JWTSigner.getBytes(StandardCharsets.UTF_8));
        Map<String, Object> result = new HashMap<>(7);
        result.put("token", token);
        result.put("username", loginUser.getRealUsername());
        result.put("gender", loginUser.getGender());
        result.put("introduction", loginUser.getIntroduction());
        result.put("headPortrait", loginUser.getHeadPortrait());
        result.put("role", loginUser.getRole());
        return result;
    }

    @Override
    public void logout() {
        Long userId = UserUtil.getNowUser().getId();
        log.info("用户ID为 {} 的用户登出", userId);
        redisTemplate.delete(userId);
    }

    @Override
    public boolean removeById(Serializable id) {
        LoginUser nowUser = UserUtil.getNowUser();
        // 如果当前用户不是管理员，且需要删除其他用户
        if (!nowUser.getRole().equals(UserRole.ADMIN) && !nowUser.getId().equals(id)) {
            throw new MessagePromptException("您不能删除其他用户");
        }

        // 删除用户的登录信息
        redisTemplate.delete(id);

        User user = baseMapper.selectById(id);

        String key = user.getHeadPortrait();
        // 不是默认头像
        if (!DefaultValue.DEFAULT_HEADPORTRAIT_KEY.equals(key)) {
            // 删除头像
            COSUtil.deleteFile(key);
        }

        if (user.getRole().equals(UserRole.TEACHER)) {
            rabbitTemplate.convertAndSend("deleteCourseByTeacherId", id);
            teacherService.removeById(id);
        }

        if (user.getRole().equals(UserRole.STUDENT)) {
            studentService.removeById(id);
        }

        return super.removeById(id);
    }
}
