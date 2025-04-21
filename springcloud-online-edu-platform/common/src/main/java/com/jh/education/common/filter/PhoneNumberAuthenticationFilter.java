package com.jh.education.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jh.education.common.config.PhoneNumberAuthenticationToken;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.util.Map;

/**
 * 模仿 UsernamePasswordAuthenticationFilter 获取前端传递的 手机号、验证码
 */
public class PhoneNumberAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/login", "POST");

    // 参数名
    private String phoneNumberParameter = "phoneNumber";
    private String verificationCodeParameter = "verificationCode";

    public PhoneNumberAuthenticationFilter() {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER);
    }

    /**
     * 用来获取前端传递的手机号和验证码，然后调用 authenticate 方法进行认证
     *
     * @param request
     * @param response
     * @return
     * @throws AuthenticationException
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        if (!"POST".equals(request.getMethod())) {
            throw new AuthenticationServiceException("请求方式有误: " + request.getMethod());
        }
        //如果请求的参数格式不是json，直接异常
        if (!request.getContentType().equals(MediaType.APPLICATION_JSON_VALUE)) {
            throw new AuthenticationServiceException("参数不是json：" + request.getMethod());
        }
        // 用户以json的形式传参的情况下
        String phoneNumber = null;
        String verificationCode = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> map = objectMapper.readValue(request.getInputStream(), Map.class);
            phoneNumber = map.get(phoneNumberParameter);
            verificationCode = map.get(verificationCodeParameter);
        } catch (IOException e) {
            throw new AuthenticationServiceException("参数不对：" + request.getMethod());
        }

        if (phoneNumber == null) {
            phoneNumber = "";
        }
        if (verificationCode == null) {
            verificationCode = "";
        }

        phoneNumber = phoneNumber.trim();
        // 封装手机号、验证码，后面框架会从中拿到 手机号， 调用我们的 LoginPhoneService 获取用户
        PhoneNumberAuthenticationToken authRequest = new PhoneNumberAuthenticationToken(phoneNumber, verificationCode);
        //设置ip、sessionId信息
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    protected void setDetails(HttpServletRequest request, PhoneNumberAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }
}