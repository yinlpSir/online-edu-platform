package com.jh.education.live.bean;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LiveInfo {
    private Long classId;
    private String className;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private Long teacherId;
    private String teacherName;
    private String teacherIntroduction;
}
