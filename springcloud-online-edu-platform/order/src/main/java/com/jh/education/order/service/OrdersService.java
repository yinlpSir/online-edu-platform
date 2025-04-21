package com.jh.education.order.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jh.education.order.dto.OrderGenerateDTO;
import com.jh.education.order.dto.OrderQueryDTO;
import com.jh.education.order.dto.OrderUpdateDTO;
import com.jh.education.order.entity.Orders;
import com.jh.education.order.vo.OrderVO;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author jh
 * @since 2023-07-19
 */
public interface OrdersService extends IService<Orders> {

    boolean generateOrder(OrderGenerateDTO dto);

    boolean updateOrder(OrderUpdateDTO dto);

    Page<OrderVO> getAllOrder(OrderQueryDTO dto);
}
