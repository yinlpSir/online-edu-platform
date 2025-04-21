package com.jh.education.user.controller;

import com.jh.education.common.bean.CommonResult;
import com.jh.education.common.bean.MyPage;
import com.jh.education.common.util.UserUtil;
import com.jh.education.user.dto.StudentRegisterDTO;
import com.jh.education.user.dto.StudentUpdateDTO;
import com.jh.education.user.service.StudentService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/user/student")
public class StudentController {
    @Resource
    private StudentService studentService;

    @PostMapping("/register")
    @PreAuthorize("isAnonymous()")
    public CommonResult registerStudent(@Valid StudentRegisterDTO registerDTO) {
        log.info("学生注册：{}", registerDTO);
        studentService.registerStudent(registerDTO);
        return CommonResult.success("注册成功");
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('student')")
    public CommonResult updateStudent(@Valid StudentUpdateDTO updateDTO) {
        log.info("学生信息更新：{}", updateDTO);
        return CommonResult.success("更新成功", studentService.updateStudent(updateDTO));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('admin')")
    public CommonResult getAllStudent(@Valid MyPage myPage, String phoneNumber) {
        log.info("分页查询所有学生信息：{}，手机号：{}", myPage, phoneNumber);
        return CommonResult.success("查询成功", studentService.getAllStudent(myPage, phoneNumber));
    }

    @GetMapping("/currentStudent")
    @PreAuthorize("hasAnyAuthority('student')")
    public CommonResult getCurrentStudentDetail() {
        Long id = UserUtil.getNowUser().getId();
        log.info("查询id为 {} 的学生详细信息", id);
        return CommonResult.success("查询成功", studentService.getStudentDetail(id));
    }
}
