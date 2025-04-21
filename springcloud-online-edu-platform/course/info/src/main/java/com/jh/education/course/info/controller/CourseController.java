package com.jh.education.course.info.controller;

import com.alipay.api.AlipayApiException;
import com.jh.education.common.bean.CommonResult;
import com.jh.education.course.info.dto.CourseAddDTO;
import com.jh.education.course.info.dto.CourseManagementDTO;
import com.jh.education.course.info.dto.CourseQueryDTO;
import com.jh.education.course.info.dto.CourseUpdateDTO;
import com.jh.education.course.info.service.CourseService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
@RequestMapping("/course/info")
public class CourseController {
    @Resource
    private CourseService courseService;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/banner")
    public CommonResult getBanner() {
        log.info("获取轮播图信息");
        return CommonResult.success("获取轮播图成功", courseService.getBanner());
    }

    @GetMapping("/all")
    public CommonResult getAllCourse(@Valid CourseQueryDTO queryDTO) {
        log.info("按条件查询所有课程：{}", queryDTO);
        return CommonResult.success("查询成功", courseService.getAllCourse(queryDTO));
    }

    @PutMapping("/authenticate/{id}/{isPassed}")
    @PreAuthorize("hasAnyAuthority('admin')")
    public CommonResult authenticateCourse(@PathVariable Long id, @PathVariable Boolean isPassed) {
        log.info("认证id为 {} 的课程，认证结果为：{}", id, isPassed);
        courseService.authenticateCourse(id, isPassed);
        return CommonResult.success(isPassed ? "审核通过" : "审核未通过");
    }

    @GetMapping("/detail/{id}")
    public CommonResult getCourseDetail(@PathVariable Long id) {
        log.info("查询id为 {} 的课程详情", id);
        return CommonResult.success("查询成功", courseService.getCourseDetail(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('teacher')")
    public CommonResult addCourse(@Valid CourseAddDTO dto) {
        log.info("添加课程：{}", dto);
        Long id = courseService.addCourse(dto);
        Map<String, String> map = new HashMap<>();
        map.put("courseId", id.toString());
        return CommonResult.success("添加成功", map);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('teacher')")
    public CommonResult deleteCourse(@PathVariable Long id) {
        log.info("删除id为 {} 的课程", id);
        rabbitTemplate.convertAndSend("deleteCourseExchange", "", id);
        return CommonResult.success("删除成功");
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('teacher','admin')")
    public CommonResult updateCourse(@Valid CourseUpdateDTO dto) {
        log.info("更新课程：{}", dto);
        courseService.updateCourse(dto);
        return CommonResult.success("更新成功");
    }

    @PostMapping("/buyCourse/{id}")
    @PreAuthorize("hasAnyAuthority('student')")
    public CommonResult buyCourse(@PathVariable Long id) throws AlipayApiException {
        log.info("购买id为 {} 的课程", id);
        return CommonResult.success("调用成功", courseService.buyCourse(id));
    }

    @PostMapping("/buyCourseIsSuccessful")
    public CommonResult buyCourseIsSuccessful(HttpServletRequest request) throws AlipayApiException {
        Map<String, String[]> parameterMap = request.getParameterMap();
        log.info("购买结果：{}", parameterMap);
        courseService.buyCourseIsSuccessful(parameterMap);
        return CommonResult.success("支付成功");
    }

    @GetMapping("/allBoughtCourse")
    @PreAuthorize("hasAnyAuthority('student')")
    public CommonResult getAllBoughtCourse(@Valid CourseQueryDTO dto) {
        log.info("查询学生已购买的课程：{}", dto);
        return CommonResult.success("查询成功", courseService.getAllBoughtCourse(dto));
    }

    @GetMapping("/courseManagement")
    @PreAuthorize("hasAnyAuthority('teacher','admin')")
    public CommonResult courseManagement(@Valid CourseManagementDTO dto) {
        log.info("课程管理：{}", dto);
        return CommonResult.success("查询成功", courseService.courseManagement(dto));
    }

    @GetMapping("/courseName/{id}")
    public String getCourseNameById(@PathVariable Long id) {
        return courseService.getCourseNameById(id);
    }

    @GetMapping("/isFreeCourse/{id}")
    public boolean isFreeCourse(@PathVariable Long id) {
        return courseService.isFreeCourse(id);
    }

    @PostMapping("/courseNumber/{id}/{number}")
    public boolean setCourseNumber(@PathVariable Long id, @PathVariable Integer number) {
        return courseService.setCourseNumber(id, number);
    }
}