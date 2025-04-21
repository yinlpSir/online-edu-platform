package com.jh.education.course.plastic.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jh.education.course.plastic.dto.CoursePlasticAddDTO;
import com.jh.education.course.plastic.dto.CoursePlasticUpdateDTO;
import com.jh.education.course.plastic.entity.CoursePlastic;
import com.jh.education.feign.vo.CoursePlasticVO;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author jh
 * @since 2023-07-19
 */
public interface CoursePlasticService extends IService<CoursePlastic> {
    boolean updateCoursePlastic(CoursePlasticUpdateDTO dto);

    boolean deleteCoursePlastic(Long id);

    boolean addCoursePlastic(CoursePlasticAddDTO dto);

    List<CoursePlasticVO> getCoursePlasticByCourseId(Long courseId);

    boolean deleteCoursePlasticByCourse(Long courseId);
}
