package com.jh.education.course.classinfo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jh.education.course.classinfo.dto.ClassAddDTO;
import com.jh.education.course.classinfo.dto.ClassUpdateDTO;
import com.jh.education.course.classinfo.entity.Class;
import com.jh.education.feign.vo.ClassVO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author jh
 * @since 2023-07-19
 */
public interface ClassService extends IService<Class> {
    LocalDateTime getCourseStartTime(Long courseId);

    List<ClassVO> getAllClassByCourseId(Long courseId);

    boolean updateClass(ClassUpdateDTO dto);

    boolean deleteClass(Long id);

    boolean addClass(ClassAddDTO dto);

    boolean saveClassProcess(Long id, Long classProcess);

    boolean deleteClassByCourseId(Long courseId);
}
