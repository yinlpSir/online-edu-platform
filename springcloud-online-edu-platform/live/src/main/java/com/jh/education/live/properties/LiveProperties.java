package com.jh.education.live.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "live")
@Data
public class LiveProperties {
    private String openID;
    private String openToken;
}
