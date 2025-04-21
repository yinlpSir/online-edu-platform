package com.jh.education.common.filter;

import cn.hutool.core.convert.NumberWithFormat;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jh.education.common.bean.CommonResult;
import com.jh.education.common.bean.LoginUser;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public abstract class LoginFilter extends OncePerRequestFilter {
    @Resource
    private RedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(authorization)) {
            filterChain.doFilter(request, response);
            return;
        }
        JWT jwt = JWTUtil.parseToken(authorization.substring("Bearer ".length()));
        long expirationTimeMillis = ((NumberWithFormat) jwt.getPayload().getClaim("exp")).longValue() * 1000;
        long currentTimeMillis = System.currentTimeMillis();
        // token 已过期
        if (currentTimeMillis > expirationTimeMillis) {
            long userId = ((NumberWithFormat) jwt.getPayload().getClaim("userId")).longValue();
            redisTemplate.opsForValue().getAndDelete(userId);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write(new ObjectMapper().writeValueAsString(CommonResult.fail(HttpServletResponse.SC_FORBIDDEN, "无效的token")));
            return;
        }
        long userId = ((NumberWithFormat) jwt.getPayload().getClaim("userId")).longValue();
        LoginUser loginUser = (LoginUser) redisTemplate.opsForValue().get(userId);
        if (loginUser == null) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write(new ObjectMapper().writeValueAsString(CommonResult.fail(HttpServletResponse.SC_FORBIDDEN, "用户未登录")));
            return;
        }

        setAuthentication(request, response, filterChain, loginUser);
    }

    public abstract void setAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, LoginUser loginUser) throws IOException, ServletException;
}
