package com.jh.education.order.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.jh.education.common.bean.LoginUser;
import com.jh.education.common.constant.UserRole;
import com.jh.education.common.util.UserUtil;
import com.jh.education.order.dto.OrderGenerateDTO;
import com.jh.education.order.dto.OrderQueryDTO;
import com.jh.education.order.dto.OrderUpdateDTO;
import com.jh.education.order.entity.Orders;
import com.jh.education.order.mapper.OrdersMapper;
import com.jh.education.order.service.OrdersService;
import com.jh.education.order.vo.OrderVO;
import jakarta.annotation.Resource;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author jh
 * @since 2023-07-19
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Override
    public boolean generateOrder(OrderGenerateDTO dto) {
        Orders orders = new Orders();
        BeanUtil.copyProperties(dto, orders);
        boolean save = save(orders);
        // 1.准备消息
        Message message = MessageBuilder
                .withBody(orders.getId().toString().getBytes(StandardCharsets.UTF_8))
                .setHeader("x-delay", 30 * 60 * 1000)
                .build();
        // 2.发送消息
        rabbitTemplate.convertAndSend("delay.exchange", "orderExpired", message);
        return save;
    }

    @Override
    public boolean updateOrder(OrderUpdateDTO dto) {
        Orders orders = new Orders();
        orders.setOrderStatus(dto.getOrderStatus());
        LambdaUpdateWrapper<Orders> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Orders::getOrderNo, dto.getOrderNo());
        return update(orders, wrapper);
    }

    @Override
    public Page<OrderVO> getAllOrder(OrderQueryDTO dto) {
        MPJLambdaWrapper<Orders> wrapper = new MPJLambdaWrapper<>();
        LoginUser nowUser = UserUtil.getNowUser();
        // 学生只能查询自己的订单
        if (nowUser.getRole().equals(UserRole.STUDENT)) {
            wrapper.eq(Orders::getUserId, nowUser.getId());
        }
        return baseMapper.selectJoinPage(Page.of(dto.getCurrentPage(), dto.getPageSize()), OrderVO.class, wrapper);
    }
}
