package com.jh.education.course.info.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
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
public class Course {

    @TableId
    private Long id;

    private String courseName;

    private String description;

    private String courseCover;

    private BigDecimal price;

    private String grade;

    private Integer number;

    private String subject;

    private Integer purchaseQuantity;

    private Byte isPassed;

    private Long teacherId;

    private Boolean isVideoDraggable;

    private Integer courseType;

    @TableLogic
    private Boolean isDeleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
