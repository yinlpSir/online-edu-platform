package com.jh.education.feign.client;

import com.jh.education.feign.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "courseInfoService", path = "/course/info", configuration = {FeignConfig.class})
public interface CourseClient {
    @GetMapping("/studentBuyCourse/studentIsBougutCourse/{courseId}")
    boolean studentIsBougutCourse(@PathVariable Long courseId);

    @GetMapping("/courseName/{id}")
    String getCourseNameById(@PathVariable Long id);

    @GetMapping("/isFreeCourse/{id}")
    boolean isFreeCourse(@PathVariable Long id);

    @PostMapping("/courseNumber/{id}/{number}")
    boolean setCourseNumber(@PathVariable Long id, @PathVariable Integer number);
}