package com.jh.education.common.bean;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MyPage {
    @NotNull(message = "没有提供当前页")
    @Min(value = 1, message = "当前页必须大于或等于1")
    private Long currentPage;

    @Min(value = 1, message = "页的大小必须大于或等于1")
    private Long pageSize = 5L;
}