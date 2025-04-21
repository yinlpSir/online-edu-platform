package com.jh.education.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jh.education.common.bean.MyPage;
import com.jh.education.feign.vo.TeacherInfo;
import com.jh.education.user.dto.TeacherRegisterDTO;
import com.jh.education.user.dto.TeacherUpdateDTO;
import com.jh.education.user.entity.Teacher;
import com.jh.education.user.vo.TeacherVO;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author jh
 * @since 2023-07-14
 */
public interface TeacherService extends IService<Teacher> {
    boolean registerTeacher(TeacherRegisterDTO registerDTO);

    boolean updateTeacher(TeacherUpdateDTO updateDTO);

    void authenticateTeacher(Long id, Boolean isAuthenticated);

    Page<TeacherVO> getAllTeacher(MyPage myPage);

    List<TeacherInfo> getTeacherInfoByTeacherName(String teacherName);

    TeacherInfo getOneTeacherInfo(Long id);
}
