package com.jh.education.feign.client;

import com.jh.education.feign.config.FeignConfig;
import com.jh.education.feign.vo.CoursePlasticVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(value = "coursePlasticService", path = "/course/plastic", configuration = {FeignConfig.class})
public interface CoursePlasticClient {
    @GetMapping("/{courseId}")
    List<CoursePlasticVO> getCoursePlasticByCourseId(@PathVariable Long courseId);
}
