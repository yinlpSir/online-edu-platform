package com.jh.education.course.info.controller;

import com.jh.education.course.info.service.StudentBuyCourseService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author jh
 * @since 2023-07-19
 */
@RestController
@RequestMapping("/course/info/studentBuyCourse")
public class StudentBuyCourseController {
    @Resource
    private StudentBuyCourseService studentBuyCourseService;

    @GetMapping("/studentIsBougutCourse/{courseId}")
    @PreAuthorize("hasAnyAuthority('student')")
    public boolean studentIsBougutCourse(@PathVariable Long courseId) {
        return studentBuyCourseService.studentIsBougutCourse(courseId);
    }

}