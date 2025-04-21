package com.jh.education.feign.client;

import com.jh.education.feign.config.FeignConfig;
import com.jh.education.feign.vo.ClassVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.List;

@FeignClient(value = "classInfoService", path = "/course/class", configuration = {FeignConfig.class})
public interface ClassClient {
    @GetMapping("/allByCourseId/{courseId}")
    List<ClassVO> getAllClassByCourseId(@PathVariable Long courseId);

    @GetMapping("/courseStartTime/{courseId}")
    LocalDateTime getCourseStartTime(@PathVariable Long courseId);
}