package com.jh.education.course.info.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jh.education.course.info.entity.StudentBuyCourse;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author jh
 * @since 2023-07-19
 */
public interface StudentBuyCourseService extends IService<StudentBuyCourse> {
    boolean studentIsBougutCourse(Long courseId);

    boolean addBuyCourseRecord(Long courseId, Long studentId);

    boolean deleteBuyCourseRecordByCourseId(Long courseId);
}
