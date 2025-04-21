package com.jh.education.course.classinfo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author jh
 * @since 2023-07-19
 */
@Data
public class Class {
    @TableId
    private Long id;

    private String className;

    private Integer number;

    private String videoPath;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Long lastTime;

    private Long courseId;

    @TableLogic
    private Boolean isDeleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
