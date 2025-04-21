package com.jh.education.course.plastic.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CoursePlasticUpdateDTO {
    @NotNull(message = "资料ID不能为空")
    private Long id;
    private String plasticName;
    private MultipartFile plastic;
}
