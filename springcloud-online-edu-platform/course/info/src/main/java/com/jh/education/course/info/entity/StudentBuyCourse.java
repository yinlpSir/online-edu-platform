package com.jh.education.course.info.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
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
@Data
@TableName("student_buy_course")
public class StudentBuyCourse {

    @TableId
    private Long id;

    private Long studentId;

    private Long courseId;

    @TableLogic
    private Boolean isDeleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
