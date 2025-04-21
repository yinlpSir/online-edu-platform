package com.jh.education.course.info.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jh.education.course.info.dto.CommentAddDTO;
import com.jh.education.course.info.entity.Comment;
import com.jh.education.course.info.vo.CommentVO;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author jh
 * @since 2023-07-19
 */
public interface CommentService extends IService<Comment> {
    List<CommentVO> getCommentByCourseId(Long courseId);

    boolean addComment(CommentAddDTO dto);

    boolean deleteComment(Long id);

    boolean deleteCommentByCourseId(Long courseId);
}
