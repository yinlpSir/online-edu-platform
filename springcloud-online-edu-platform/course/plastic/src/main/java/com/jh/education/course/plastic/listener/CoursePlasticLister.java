package com.jh.education.course.plastic.listener;

import com.jh.education.course.plastic.service.CoursePlasticService;
import jakarta.annotation.Resource;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class CoursePlasticLister {
    @Resource
    private CoursePlasticService coursePlasticService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "coursePlasticMQ"),
            exchange = @Exchange(name = "deleteCourseExchange", type = ExchangeTypes.FANOUT)
    ))
    public void deleteCoursePlasticByCourse(Long courseId) {
        coursePlasticService.deleteCoursePlasticByCourse(courseId);
    }
}
