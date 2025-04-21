package com.jh.education.course.plastic.controller;

import com.jh.education.common.bean.CommonResult;
import com.jh.education.course.plastic.dto.CoursePlasticAddDTO;
import com.jh.education.course.plastic.dto.CoursePlasticUpdateDTO;
import com.jh.education.course.plastic.service.CoursePlasticService;
import com.jh.education.feign.vo.CoursePlasticVO;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author jh
 * @since 2023-07-19
 */
@Slf4j
@RestController
@RequestMapping("/course/plastic")
public class CoursePlasticController {
    @Resource
    private CoursePlasticService coursePlasticService;

    @GetMapping("/{courseId}")
    public List<CoursePlasticVO> getCoursePlasticByCourseId(@PathVariable("courseId") Long courseId) {
        log.info("获取课程id为 {} 的课程资料", courseId);
        return coursePlasticService.getCoursePlasticByCourseId(courseId);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('teacher')")
    public CommonResult addCoursePlastic(@Valid CoursePlasticAddDTO dto) {
        log.info("添加课程资料：{}", dto);
        coursePlasticService.addCoursePlastic(dto);
        return CommonResult.success("添加成功");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('teacher')")
    public CommonResult deleteCoursePlastic(@PathVariable Long id) {
        log.info("删除id为 {} 的课程资料", id);
        coursePlasticService.deleteCoursePlastic(id);
        return CommonResult.success("删除成功");
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('teacher')")
    public CommonResult updateCoursePlastic(@Valid CoursePlasticUpdateDTO dto) {
        log.info("更新课程资料：{}", dto);
        coursePlasticService.updateCoursePlastic(dto);
        return CommonResult.success("更新成功");
    }
}
