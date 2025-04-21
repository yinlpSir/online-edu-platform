package com.jh.education.common.filter;

import com.jh.education.common.bean.LoginUser;
import com.jh.education.common.util.UserUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginByPasswordFilter extends LoginFilter {
    @Override
    public void setAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, LoginUser loginUser) throws IOException, ServletException {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, UserUtil.getAuthoritiesByUserRole(loginUser.getRole()));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }
}