package com.jh.education.course.classinfo.listener;

import com.jh.education.course.classinfo.service.ClassService;
import jakarta.annotation.Resource;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ClassLister {
    @Resource
    private ClassService classService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "classMQ"),
            exchange = @Exchange(name = "deleteCourseExchange", type = ExchangeTypes.FANOUT)
    ))
    public void deleteClassByCourse(Long courseId) {
        classService.deleteClassByCourseId(courseId);
    }
}
