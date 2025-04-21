package com.jh.education.common.user.impl;

import com.jh.education.common.bean.LoginUser;
import com.jh.education.common.constant.UserRole;
import com.jh.education.common.exception.MessagePromptException;
import com.jh.education.common.mapper.CommonUserMapper;
import com.jh.education.common.util.COSUtil;
import com.jh.education.common.util.UserUtil;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private CommonUserMapper commonUserMapper;

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        LoginUser loginUser = commonUserMapper.selectOneByPhoneNumber(phoneNumber);
        loginUser.setHeadPortrait(COSUtil.getFileUrl(loginUser.getHeadPortrait()));
        //如果查询不到数据就通过抛出异常来给出提示
        if (Objects.isNull(loginUser)) {
            throw new MessagePromptException("用户不存在");
        }
        // 如果没有该手机所对应的验证码，则代表使用的是 手机号 + 密码 登录
        if (redisTemplate.opsForValue().get(phoneNumber) == null) {
            loginUser.setVerificationCode(loginUser.getPwd());
        }
        Byte role = loginUser.getRole();
        if (!role.equals(UserRole.TEACHER)) {
            loginUser.setAuthorities(UserUtil.getAuthoritiesByUserRole(role));
        } else {
            if (commonUserMapper.teacherIsAuthentication(loginUser.getId())) {
                loginUser.setAuthorities(UserUtil.getAuthoritiesByUserRole(role));
            } else {
                List<GrantedAuthority> authorities = new ArrayList<>();
                GrantedAuthority authority = new SimpleGrantedAuthority("unauthentication_teacher");
                authorities.add(authority);
                loginUser.setAuthorities(authorities);
            }
        }
        return loginUser;
    }
}
