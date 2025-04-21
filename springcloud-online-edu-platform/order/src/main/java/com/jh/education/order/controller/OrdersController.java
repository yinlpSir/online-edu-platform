package com.jh.education.order.controller;

import com.jh.education.common.bean.CommonResult;
import com.jh.education.order.dto.OrderQueryDTO;
import com.jh.education.order.service.OrdersService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author jh
 * @since 2023-07-19
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrdersController {

    @Resource
    private OrdersService ordersService;

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('admin','student')")
    // 在Spring中，使用 @GetMapping 注解的方法应该至少是包私有（默认）、受保护的或公共的。
    public CommonResult getAllOrder(@Valid OrderQueryDTO dto) {
        log.info("订单查询：{}", dto);
        return CommonResult.success("查询成功", ordersService.getAllOrder(dto));
    }
}
