package com.jh.education.user.controller;

import com.jh.education.common.bean.CommonResult;
import com.jh.education.common.bean.MyPage;
import com.jh.education.feign.vo.TeacherInfo;
import com.jh.education.user.dto.TeacherRegisterDTO;
import com.jh.education.user.dto.TeacherUpdateDTO;
import com.jh.education.user.service.TeacherService;
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
 * @since 2023-07-14
 */
@Slf4j
@RestController
@RequestMapping("/user/teacher")
public class TeacherController {
    @Resource
    private TeacherService teacherService;

    @PutMapping("/authenticate/{id}/{isAuthenticated}")
    @PreAuthorize("hasAnyAuthority('admin')")
    public CommonResult authenticateTeacher(@PathVariable Long id, @PathVariable("isAuthenticated") Boolean isAuthenticated) {
        log.info("认证id为 {} 的教师，认证结果为：{}", id, isAuthenticated);
        teacherService.authenticateTeacher(id, isAuthenticated);
        return CommonResult.success(isAuthenticated ? "认证通过" : "认证不通过");
    }

    @PostMapping("/register")
    @PreAuthorize("isAnonymous()")
    public CommonResult registerTeacher(@Valid TeacherRegisterDTO registerDTO) {
        log.info("教师注册：{}", registerDTO);
        teacherService.registerTeacher(registerDTO);
        return CommonResult.success("注册成功");
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('teacher','unauthentication_teacher')")
    public CommonResult updateTeacher(@Valid TeacherUpdateDTO updateDTO) {
        log.info("教师更新：{}", updateDTO);
        teacherService.updateTeacher(updateDTO);
        return CommonResult.success("更新成功");
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('admin')")
    public CommonResult getAllTeacher(@Valid MyPage page) {
        log.info("分页查询所有教师信息：{}", page);
        return CommonResult.success("查询成功", teacherService.getAllTeacher(page));
    }

    // 以下方法为其他服务提供远程调用
    @GetMapping("/getTeacherInfoByTeacherName")
    public List<TeacherInfo> getTeacherInfoByTeacherName(String teacherName) {
        return teacherService.getTeacherInfoByTeacherName(teacherName);
    }

    @GetMapping("/getOneTeacherInfo/{id}")
    public TeacherInfo getOneTeacherInfo(@PathVariable Long id) {
        return teacherService.getOneTeacherInfo(id);
    }
}
