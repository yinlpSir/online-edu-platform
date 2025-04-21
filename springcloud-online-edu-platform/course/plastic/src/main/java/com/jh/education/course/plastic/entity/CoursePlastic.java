package com.jh.education.course.plastic.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("course_plastic")
public class CoursePlastic {
    @TableId
    private Long id;

    private String plasticName;

    private String plasticPath;

    private Long plasticSize;

    private Long courseId;

    @TableLogic
    private Boolean isDeleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
