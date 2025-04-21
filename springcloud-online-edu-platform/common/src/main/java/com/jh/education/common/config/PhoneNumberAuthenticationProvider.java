package com.jh.education.common.config;

import cn.hutool.core.util.StrUtil;
import com.jh.education.common.bean.LoginUser;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

/**
 * 主要实现 authenticate 方法，写我们自己的认证逻辑
 */
@Component
public class PhoneNumberAuthenticationProvider implements AuthenticationProvider {

    @Resource
    private UserDetailsService userDetailsService;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 手机号、验证码的认证逻辑
     *
     * @param authentication 其实就是我们封装的 PhoneNumberAuthenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        PhoneNumberAuthenticationToken token = (PhoneNumberAuthenticationToken) authentication;
        String phoneNumber = (String) authentication.getPrincipal();// 获取手机号
        String verificationCode = (String) authentication.getCredentials(); // 获取输入的验证码

        // 1. 从 redis 中获取验证码
        String redisVerificationCode = (String) redisTemplate.opsForValue().get(phoneNumber);

        if (StrUtil.isBlankIfStr(redisVerificationCode)) {
            throw new BadCredentialsException("验证码已经过期，请重新发送验证码");
        }
        if (!verificationCode.equals(redisVerificationCode)) {
            throw new BadCredentialsException("验证码不正确");
        }
        // 2. 根据手机号查询用户信息
        LoginUser loginUser = (LoginUser) userDetailsService.loadUserByUsername(phoneNumber);
        if (loginUser == null) {
            throw new BadCredentialsException("用户不存在，请注册");
        }
        // 3. 把用户封装到 PhoneNumberAuthenticationToken 中，
        // 后面就可以使用 SecurityContextHolder.getContext().getAuthentication(); 获取当前登陆用户信息
        PhoneNumberAuthenticationToken authenticationResult = new PhoneNumberAuthenticationToken(loginUser, verificationCode, loginUser.getAuthorities());
        authenticationResult.setDetails(token.getDetails());
        return authenticationResult;
    }

    /**
     * 判断是上面 authenticate 方法的 authentication 参数，是哪种类型
     * Authentication 是个接口，实现类有很多，目前我们最熟悉的就是 PhoneNumberAuthenticationToken、UsernamePasswordAuthenticationToken
     * 很明显，我们只支持 PhoneNumberAuthenticationToken，因为它封装的是手机号、验证码
     *
     * @param authentication
     * @return
     */
    @Override
    public boolean supports(Class<?> authentication) {
        // 如果参数是 PhoneNumberAuthenticationToken 类型，返回true
        return (PhoneNumberAuthenticationToken.class.isAssignableFrom(authentication));
    }
}