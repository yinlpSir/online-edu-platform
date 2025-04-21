package com.jh.education.order.listener;

import cn.hutool.core.bean.BeanUtil;
import com.jh.education.order.dto.OrderGenerateDTO;
import com.jh.education.order.dto.OrderUpdateDTO;
import com.jh.education.order.service.OrdersService;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OrderListener {
    @Resource
    private OrdersService ordersService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "generateOrderMQ"),
            exchange = @Exchange(name = "orderExchange"),
            key = {"generate"}
    ))
    public void generateOrder(Map<String, Object> map) {
        OrderGenerateDTO dto = new OrderGenerateDTO();
        BeanUtil.fillBeanWithMap(map, dto, false);
        ordersService.generateOrder(dto);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "updateOrderMQ"),
            exchange = @Exchange(name = "orderExchange"),
            key = {"update"}
    ))
    public void updateOrder(Map<String, Object> map) {
        OrderUpdateDTO dto = new OrderUpdateDTO();
        BeanUtil.fillBeanWithMap(map, dto, false);
        ordersService.updateOrder(dto);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "delay.queue3"),
            exchange = @Exchange(name = "delay.exchange", delayed = "true"),
            key = {"orderExpired"}
    ))
    public void listenOrderExpired(Long id) {
        // 未支付
        if (ordersService.getById(id).getOrderStatus() != (byte) 1) {
            ordersService.removeById(id);
        }
    }
}
