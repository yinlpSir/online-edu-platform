package com.jh.education.common.util;

import com.jh.education.common.bean.LoginUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserUtil {

    private UserUtil() {
    }

    public static Collection<GrantedAuthority> getAuthoritiesByUserRole(Byte userRole) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        GrantedAuthority authority = null;
        switch (userRole) {
            case 1 -> authority = new SimpleGrantedAuthority("admin");
            case 2 -> authority = new SimpleGrantedAuthority("teacher");
            case 3 -> authority = new SimpleGrantedAuthority("student");
            default -> {
            }
        }
        authorities.add(authority);
        return authorities;
    }

    public static LoginUser getNowUser() {
        return (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
