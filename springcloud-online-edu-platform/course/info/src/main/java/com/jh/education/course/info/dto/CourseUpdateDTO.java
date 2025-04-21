package com.jh.education.course.info.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Data
public class CourseUpdateDTO {
    @NotNull(message = "课程ID不能为空")
    private Long id;
    private String courseName;
    private String description;
    private MultipartFile courseCover;
    private BigDecimal price;
    private String[] grade;
    private Integer number;
    private String subject;
    private Boolean isVideoDraggable;
    private Integer courseType;
}
