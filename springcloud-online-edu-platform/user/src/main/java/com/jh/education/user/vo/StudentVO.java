package com.jh.education.user.vo;

import lombok.Data;

@Data
public class StudentVO {
    private String id;
    private String username;
    private String headPortrait;
    private String phoneNumber;
    private Boolean gender;
    private String grade;
    private String introduction;
}