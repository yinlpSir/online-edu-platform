package com.jh.education.feign.vo;

import lombok.Data;

@Data
public class LiveVO {
    // 直播状态：1 未开始；2 正在直播；3 已结束
    private Integer liveStatus;

    // 教师直播URL
    private String liveUrl;

    // 回放URL
    private String playbackUrl;

    // 学生看课URL
    private String watchUrl;
}
