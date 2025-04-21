package com.jh.education.course.info.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CourseVO {
    private String id;
    private String courseCover;
    private String courseName;
    private BigDecimal price;
    private String grade;
    private Integer number;
    private String subject;
    private Integer purchaseQuantity;
    private LocalDateTime startTime;
    private Long teacherId;
    private String teacherName;
    private String teacherHeadPortrait;
    private Boolean isBought;
}