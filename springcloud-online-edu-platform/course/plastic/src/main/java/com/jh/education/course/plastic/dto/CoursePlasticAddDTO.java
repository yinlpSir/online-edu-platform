package com.jh.education.course.plastic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CoursePlasticAddDTO {
    @NotBlank(message = "资料名不能为空")
    private String plasticName;
    @NotNull(message = "资料不能为空")
    private MultipartFile plastic;
    @NotNull(message = "课程ID不能为空")
    private Long courseId;
}
