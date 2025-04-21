package com.jh.education.live.bean;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Live {
    @TableId
    private Long id;

    private Long classId;

    // 直播id
    private Integer courseId;

    // 教师直播URL
    private String liveUrl;

    @TableLogic
    private Boolean isDeleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
