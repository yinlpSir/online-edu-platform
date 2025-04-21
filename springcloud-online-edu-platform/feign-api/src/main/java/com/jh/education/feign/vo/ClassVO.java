package com.jh.education.feign.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClassVO {
    private String id;
    private String className;
    // 课程节数
    private Integer number;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String videoPath;
    private String lastTime;
    private Long classProcess;
    private LiveVO liveInfo;
}
