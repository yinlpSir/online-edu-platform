package com.jh.education.order.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderVO {
    private String orderNo;
    private BigDecimal orderPrice;
    private String orderSubject;
    private Byte orderStatus;
}
