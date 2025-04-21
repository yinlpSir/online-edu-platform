package com.jh.education.course.info.vo;

import com.jh.education.feign.vo.ClassVO;
import com.jh.education.feign.vo.CoursePlasticVO;
import com.jh.education.feign.vo.TeacherInfo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CourseDetailVO {
    private String id;
    private String courseName;
    private String courseCover;
    private String description;
    private BigDecimal price;
    private String grade;
    private Integer number;
    private String subject;
    private Integer purchaseQuantity;
    private Boolean isVideoDraggable;

    private TeacherInfo teacherInfo;
    private List<ClassVO> classList;
    private List<CommentVO> courseComment;
    private List<CoursePlasticVO> coursePlastic;
}
