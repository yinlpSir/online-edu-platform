package com.jh.education.course.info.controller;

import com.jh.education.common.bean.CommonResult;
import com.jh.education.course.info.dto.CommentAddDTO;
import com.jh.education.course.info.service.CommentService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/course/info/comment")
public class CommentController {
    @Resource
    private CommentService commentService;

    @GetMapping("/{courseId}")
    public CommonResult getAllCommentByCourseId(@PathVariable Long courseId) {
        log.info("查询课程ID为 {} 的所有评论", courseId);
        return CommonResult.success("查询成功", commentService.getCommentByCourseId(courseId));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('student')")
    public CommonResult addComment(@Valid @RequestBody CommentAddDTO dto) {
        log.info("添加评论：{}", dto);
        commentService.addComment(dto);
        return CommonResult.success("添加成功");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('student','teacher')")
    public CommonResult deleteComment(@PathVariable Long id) {
        log.info("删除id为 {} 的评论", id);
        commentService.deleteComment(id);
        return CommonResult.success("删除成功");
    }
}
