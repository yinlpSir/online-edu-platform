package com.jh.education.course.info;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jh.education.course.info.dto.CourseQueryDTO;
import com.jh.education.course.info.service.CourseService;
import com.jh.education.course.info.vo.CourseVO;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class InfoApplicationTests {

    @Resource
    private CourseService courseService;

    @Test
    void test() {
        CourseQueryDTO dto = new CourseQueryDTO();
        dto.setCourseName("a");
        dto.setCurrentPage(1L);
        Page<CourseVO> allCourse = courseService.getAllCourse(dto);
    }

}
