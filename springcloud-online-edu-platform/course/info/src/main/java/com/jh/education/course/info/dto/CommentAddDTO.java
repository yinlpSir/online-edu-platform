package com.jh.education.course.info.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentAddDTO {
    @NotBlank(message = "评论内容不能为空")
    private String content;
    @NotNull(message = "评论等级不能为空")
    private Byte grade;
    @NotNull(message = "课程ID不能为空")
    private Long courseId;
}