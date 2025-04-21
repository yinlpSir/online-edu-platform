package com.jh.education.order.entity;

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
public class Orders {

    @TableId
    private Long id;

    private String orderNo;

    private BigDecimal orderPrice;

    private String orderSubject;

    private Byte orderStatus;

    private Long userId;

    @TableLogic
    private Boolean isDeleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
