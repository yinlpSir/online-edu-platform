package com.jh.education.course.classinfo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.jh.education"})
@MapperScan({"com.jh.education.course.classinfo.mapper", "com.jh.education.common.mapper"})
@EnableFeignClients(basePackages = "com.jh.education.feign.client")
public class ClassinfoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClassinfoApplication.class, args);
    }

}
