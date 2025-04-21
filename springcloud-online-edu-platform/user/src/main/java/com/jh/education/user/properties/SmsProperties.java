package com.jh.education.user.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "tencent.sms")
@Data
public class SmsProperties {
    private String sdkAppId;
    private String signName;
    private String region;
}
