package com.jh.education.course.info;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.jh.education"})
@MapperScan({"com.jh.education.course.info.mapper", "com.jh.education.common.mapper"})
@EnableFeignClients(basePackages = "com.jh.education.feign.client")
public class InfoApplication {

    public static void main(String[] args) {
        SpringApplication.run(InfoApplication.class, args);
    }

}
