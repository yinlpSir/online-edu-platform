package com.jh.education.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jh.education.common.bean.MyPage;
import com.jh.education.user.dto.StudentRegisterDTO;
import com.jh.education.user.dto.StudentUpdateDTO;
import com.jh.education.user.entity.Student;
import com.jh.education.user.vo.StudentVO;

import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author jh
 * @since 2023-07-14
 */
public interface StudentService extends IService<Student> {
    boolean registerStudent(StudentRegisterDTO registerDTO);

    Map<String, Object> updateStudent(StudentUpdateDTO updateDTO);

    Page<StudentVO> getAllStudent(MyPage myPage, String phoneNumber);

    StudentVO getStudentDetail(Long id);
}
