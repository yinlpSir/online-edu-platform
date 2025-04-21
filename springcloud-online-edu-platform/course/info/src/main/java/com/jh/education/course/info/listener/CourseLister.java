package com.jh.education.course.info.listener;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jh.education.common.constant.Authentication;
import com.jh.education.course.info.entity.Course;
import com.jh.education.course.info.service.CommentService;
import com.jh.education.course.info.service.CourseService;
import jakarta.annotation.Resource;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class CourseLister {
    @Resource
    private CourseService courseService;

    @Resource
    private CommentService commentService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "courseMQ"),
            exchange = @Exchange(name = "deleteCourseExchange", type = ExchangeTypes.FANOUT)
    ))
    public void deleteCourse(Long courseId) {
        courseService.deleteCourse(courseId);
        commentService.deleteCommentByCourseId(courseId);
    }

    // queues属性：队列不存在时报错
//    @RabbitListener(queues = "deleteCourseByTeacherId")
    // queuesToDeclare 属性：队列不存在时自动创建
    @RabbitListener(queuesToDeclare = @Queue("deleteCourseByTeacherId"))
    public void deleteCourseByTeacherId(Long teacherId) {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getTeacherId, teacherId);
        courseService.list(wrapper).forEach(x -> deleteCourse(x.getId()));
    }

    @RabbitListener(queuesToDeclare = @Queue("initCourseAuthenticationStatus"))
    public void initCourseAuthenticationStatus(Long courseId) {
        Course course = new Course();
        course.setId(courseId);
        course.setIsPassed(Authentication.PENDING_REVIEW);
        courseService.updateById(course);
    }
}
