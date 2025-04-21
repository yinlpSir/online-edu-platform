package com.jh.education.course.classinfo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
public class ClassUpdateDTO {
    @NotNull(message = "课程Id不能为空")
    private Long id;
    private String className;
    private Integer number;
    private MultipartFile video;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}