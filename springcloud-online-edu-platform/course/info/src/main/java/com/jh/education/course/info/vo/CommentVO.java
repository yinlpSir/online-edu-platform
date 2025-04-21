package com.jh.education.course.info.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentVO {
    private String id;
    private String content;
    private Byte grade;
    private Long userId;
    private String username;
    private String headPortrait;
    private LocalDateTime createTime;
}