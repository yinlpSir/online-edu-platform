package com.jh.education.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderGenerateDTO {
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    @NotNull(message = "订单价格不能为空")
    private BigDecimal orderPrice;

    @NotBlank(message = "订单主题不能为空")
    private String orderSubject;

    private Long userId;
}