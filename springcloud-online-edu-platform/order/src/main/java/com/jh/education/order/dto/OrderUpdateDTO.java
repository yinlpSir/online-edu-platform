package com.jh.education.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderUpdateDTO {
    @NotBlank(message = "订单编号不能为空")
    private String orderNo;

    @NotNull(message = "订单状态不能为空")
    private Byte orderStatus;
}
