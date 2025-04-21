package com.jh.education.user.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class StudentUpdateDTO {
    private String username;
    private Boolean gender;
    private MultipartFile headPortrait;
    private String introduction;

    private String grade;
}