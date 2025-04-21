package com.jh.education.course.info.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CourseQueryDTO {
    @NotNull(message = "没有提供当前页")
    @Min(value = 1, message = "当前页必须大于或等于1")
    private Long currentPage;

    @Min(value = 1, message = "页的大小必须大于或等于1")
    private Long pageSize = 5L;
    private String courseName;
    private String subject;
    private String grade;
    private String teacherName;
    private Integer courseType;
}
