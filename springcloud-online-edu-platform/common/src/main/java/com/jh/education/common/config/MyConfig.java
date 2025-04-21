package com.jh.education.common.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.jh.education.common.filter.LoginByPasswordFilter;
import com.jh.education.common.filter.LoginByVerificationCodeFilter;
import com.jh.education.common.filter.PhoneNumberAuthenticationFilter;
import jakarta.annotation.Resource;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class MyConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Resource
    private PhoneNumberAuthenticationProvider phoneNumberAuthenticationProvider;

    @Resource
    private AuthenticationConfiguration authenticationConfiguration;

    @Resource
    private LoginByVerificationCodeFilter loginByVerificationCodeFilter;

    @Resource
    private LoginByPasswordFilter loginByPasswordFilter;

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public UsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter() throws Exception {
        UsernamePasswordAuthenticationFilter filter = new UsernamePasswordAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManager());//认证使用
        return filter;
    }

    // 配置Spring Security中的过滤器链
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 禁用CSRF（Cross Site Request Forgery，跨域请求伪造）是一种利用用户携带登录状态的cookie进行安全操作的攻击方式
                .csrf().disable()
                // 不通过Session获取SecurityContext
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                // 启用CORS支持
                .and().cors();

//        http.addFilterBefore(loginByVerificationCodeFilter, PhoneNumberAuthenticationFilter.class)
//                .addFilterBefore(loginByPasswordFilter, UsernamePasswordAuthenticationFilter.class);

        // 把 手机号认证过滤器 加到拦截器链中
        http.addFilterAt(usernamePasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(daoAuthenticationProvider());//把账户密码验证加进去

        //把 手机号认证过滤器 加到拦截器链中
        http.addFilterAfter(phoneNumberAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(phoneNumberAuthenticationProvider);//把验证逻辑加进去

        http.addFilterBefore(loginByVerificationCodeFilter, PhoneNumberAuthenticationFilter.class);

        http.addFilterBefore(loginByPasswordFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 配置路径放行规则。
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers("/doc.html", "/css/**", "/js/**", "/img/**", "/webjars/js/**", "/webjars/css/**", "/v3/api-docs/**");
    }

    @Bean
    public PhoneNumberAuthenticationFilter phoneNumberAuthenticationFilter() throws Exception {
        PhoneNumberAuthenticationFilter filter = new PhoneNumberAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManager());//认证使用
        return filter;
    }

    @Resource
    private UserDetailsService userDetailsService;

    /**
     * DaoAuthenticationProvider 是默认做账户密码认证的，现在有两种登录方式，手机号和账户密码
     * 如果不在这里声明，账户密码登录不能用
     */
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        //对默认的UserDetailsService进行覆盖
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }
}