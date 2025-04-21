package com.jh.education.course.classinfo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
public class ClassAddDTO {
    @NotBlank(message = "课程名不能为空")
    private String className;
    @NotNull(message = "课程节数不能为空")
    private Integer number;
    private MultipartFile video;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    @NotNull(message = "课程的总Id不能为空")
    private Long courseId;
}
