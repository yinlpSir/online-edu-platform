package com.jh.education.course.info.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author jh
 * @since 2023-07-19
 */
@Getter
@Setter
public class Comment {

    @TableId
    private Long id;

    private String content;

    private Byte grade;

    private Long userId;

    private Long courseId;

    @TableLogic
    private Boolean isDeleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
