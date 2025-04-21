package com.jh.education.course.classinfo.controller;

import com.jh.education.common.bean.CommonResult;
import com.jh.education.common.util.UserUtil;
import com.jh.education.course.classinfo.dto.ClassAddDTO;
import com.jh.education.course.classinfo.dto.ClassUpdateDTO;
import com.jh.education.course.classinfo.service.ClassService;
import com.jh.education.feign.vo.ClassVO;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
@RequestMapping("/course/class")
public class ClassController {
    @Resource
    private ClassService classService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('teacher')")
    public CommonResult addClass(@Valid ClassAddDTO dto) {
        log.info("添加一节课：{}", dto);
        classService.addClass(dto);
        return CommonResult.success("添加成功");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('teacher')")
    public CommonResult deleteClass(@PathVariable Long id) {
        log.info("删除一节课：{}", id);
        classService.deleteClass(id);
        return CommonResult.success("删除成功");
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('teacher')")
    public CommonResult updateClass(@Valid ClassUpdateDTO dto) {
        log.info("更新一节课：{}", dto);
        classService.updateClass(dto);
        return CommonResult.success("更新成功");
    }

    @PutMapping("/saveClassProcess/{id}/{classProcess}")
    @PreAuthorize("hasAnyAuthority('student')")
    public CommonResult saveClassProcess(@PathVariable Long id, @PathVariable Long classProcess) {
        log.info("保存用户id为{} 课程id为 {} 的观看进度 {}", UserUtil.getNowUser().getId(), id, classProcess);
        classService.saveClassProcess(id, classProcess);
        return CommonResult.success("保存成功");
    }

    @GetMapping("/allByCourseId/{courseId}")
    public List<ClassVO> getAllClassByCourseId(@PathVariable Long courseId) {
        return classService.getAllClassByCourseId(courseId);
    }

    @GetMapping("/courseStartTime/{courseId}")
    public LocalDateTime getCourseStartTime(@PathVariable Long courseId) {
        return classService.getCourseStartTime(courseId);
    }
}