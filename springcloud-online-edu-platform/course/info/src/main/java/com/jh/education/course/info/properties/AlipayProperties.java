package com.jh.education.course.info.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "alipay")
@Data
public class AlipayProperties {
    private String privateKey;
    private String alipayPublicKey;
    private String appId;
    private String gatewayUrl;
    private String returnUrl;
    private String notifyUrl;
}
