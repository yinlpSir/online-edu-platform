package com.jh.education.course.info.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jh.education.common.util.UserUtil;
import com.jh.education.course.info.entity.StudentBuyCourse;
import com.jh.education.course.info.mapper.StudentBuyCourseMapper;
import com.jh.education.course.info.service.StudentBuyCourseService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author jh
 * @since 2023-07-19
 */
@Service
public class StudentBuyCourseServiceImpl extends ServiceImpl<StudentBuyCourseMapper, StudentBuyCourse> implements StudentBuyCourseService {

    @Override
    public boolean studentIsBougutCourse(Long courseId) {
        LambdaQueryWrapper<StudentBuyCourse> wrapper = new LambdaQueryWrapper<>();
        wrapper
                .select(StudentBuyCourse::getId)
                .eq(StudentBuyCourse::getCourseId, courseId)
                .eq(StudentBuyCourse::getStudentId, UserUtil.getNowUser().getId());
        return getOne(wrapper) != null;
    }

    @Override
    public boolean addBuyCourseRecord(Long courseId, Long studentId) {
        StudentBuyCourse studentBuyCourse = new StudentBuyCourse();
        studentBuyCourse.setCourseId(courseId);
        studentBuyCourse.setStudentId(studentId);
        return save(studentBuyCourse);
    }

    @Override
    public boolean deleteBuyCourseRecordByCourseId(Long courseId) {
        LambdaUpdateWrapper<StudentBuyCourse> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(StudentBuyCourse::getCourseId, courseId);
        return remove(wrapper);
    }
}
