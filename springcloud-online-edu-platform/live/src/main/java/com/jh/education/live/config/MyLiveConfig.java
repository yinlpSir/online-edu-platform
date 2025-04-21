package com.jh.education.live.config;

import com.jh.education.live.properties.LiveProperties;
import com.jh.education.live.sdk.MTCloud;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyLiveConfig {
    @Resource
    private LiveProperties liveProperties;

    @Bean
    public MTCloud mtCloudClient() {
        return new MTCloud(liveProperties.getOpenID(), liveProperties.getOpenToken());
    }
}
