package com.jh.education.feign.client;

import com.jh.education.feign.config.FeignConfig;
import com.jh.education.feign.vo.TeacherInfo;
import com.jh.education.feign.vo.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "userService", path = "/user", configuration = {FeignConfig.class})
public interface UserClient {
    @GetMapping("/teacher/getTeacherInfoByTeacherName")
    List<TeacherInfo> getByTeacherName(@RequestParam String teacherName);

    @GetMapping("/teacher/getOneTeacherInfo/{id}")
    TeacherInfo getOneTeacherInfo(@PathVariable Long id);

    @GetMapping("/user/userInfo/{id}")
    UserInfo getUserInfo(@PathVariable Long id);
}
