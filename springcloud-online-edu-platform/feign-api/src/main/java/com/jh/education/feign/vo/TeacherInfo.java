package com.jh.education.feign.vo;

import lombok.Data;

@Data
public class TeacherInfo {
    private Long id;
    private String realName;
    private String headPortrait;
    private Boolean gender;
    private String introduction;
    private Byte isAuthenticated;
}
