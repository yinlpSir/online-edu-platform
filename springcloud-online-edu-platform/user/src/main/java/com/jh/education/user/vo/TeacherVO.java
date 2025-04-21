package com.jh.education.user.vo;

import lombok.Data;

@Data
public class TeacherVO {
    private String id;
    private String username;
    private String realName;
    private String headPortrait;
    private String phoneNumber;
    private Boolean gender;
    private String introduction;
    private String teacherCertificateImg;
    private Byte isAuthenticated;
}
