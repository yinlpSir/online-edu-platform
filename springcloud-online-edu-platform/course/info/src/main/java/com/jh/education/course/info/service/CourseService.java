package com.jh.education.course.info.service;

import com.alipay.api.AlipayApiException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jh.education.course.info.dto.CourseAddDTO;
import com.jh.education.course.info.dto.CourseManagementDTO;
import com.jh.education.course.info.dto.CourseQueryDTO;
import com.jh.education.course.info.dto.CourseUpdateDTO;
import com.jh.education.course.info.entity.Course;
import com.jh.education.course.info.vo.CourseDetailVO;
import com.jh.education.course.info.vo.CourseVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author jh
 * @since 2023-07-19
 */
public interface CourseService extends IService<Course> {
    CourseDetailVO getCourseDetail(Long id);

    Page<CourseVO> getAllCourse(CourseQueryDTO queryDTO);

    boolean authenticateCourse(Long id, Boolean isPassed);

    Long addCourse(CourseAddDTO dto);

    boolean updateCourse(CourseUpdateDTO dto);

    String buyCourse(Long id) throws AlipayApiException;

    boolean buyCourseIsSuccessful(Map<String, String[]> result) throws AlipayApiException;

    boolean deleteCourse(Long id);

    String getCourseNameById(Long id);

    boolean isFreeCourse(Long id);

    Page<CourseVO> getAllBoughtCourse(CourseQueryDTO queryDTO);

    List<Map> getBanner();

    Page<Map> courseManagement(CourseManagementDTO dto);

    boolean setCourseNumber(Long id, Integer number);
}
