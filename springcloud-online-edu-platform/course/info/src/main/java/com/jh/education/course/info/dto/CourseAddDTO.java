package com.jh.education.course.info.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Data
public class CourseAddDTO {
    @NotBlank(message = "课程名称不能为空")
    private String courseName;
    private String description;
    private MultipartFile courseCover;
    private BigDecimal price;
    private String[] grade;
    private Integer number;
    private String subject;
    private Boolean isVideoDraggable;
}