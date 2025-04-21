package com.jh.education.course.info.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.jh.education.common.bean.LoginUser;
import com.jh.education.common.constant.UserRole;
import com.jh.education.common.exception.MessagePromptException;
import com.jh.education.common.util.UserUtil;
import com.jh.education.course.info.dto.CommentAddDTO;
import com.jh.education.course.info.entity.Comment;
import com.jh.education.course.info.mapper.CommentMapper;
import com.jh.education.course.info.service.CommentService;
import com.jh.education.course.info.vo.CommentVO;
import com.jh.education.feign.client.UserClient;
import com.jh.education.feign.vo.UserInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author jh
 * @since 2023-07-19
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    @Resource
    private UserClient userClient;

    @Override
    public List<CommentVO> getCommentByCourseId(Long courseId) {
        MPJLambdaWrapper<Comment> wrapper = new MPJLambdaWrapper<>();
        wrapper
                .select(Comment::getId, Comment::getContent, Comment::getGrade, Comment::getUserId, Comment::getCreateTime)
                .eq(Comment::getCourseId, courseId);
        List<CommentVO> commentList = baseMapper.selectJoinList(CommentVO.class, wrapper);
        commentList.forEach(v -> {
            try {
                LoginUser nowUser = UserUtil.getNowUser();
                if (nowUser.getRole().equals(UserRole.STUDENT) && !nowUser.getId().equals(v.getUserId())) {
                    v.setId(null);
                }
            } catch (ClassCastException e) {
                v.setId(null);
            }
            UserInfo userInfo = userClient.getUserInfo(v.getUserId());
            v.setUsername(userInfo.getUsername());
            v.setHeadPortrait(userInfo.getHeadPortrait());
            v.setUserId(null);
            v.setCreateTime(v.getCreateTime().plusHours(8));
        });
        return commentList;
    }

    @Override
    public boolean addComment(CommentAddDTO dto) {
        Comment comment = new Comment();
        BeanUtil.copyProperties(dto, comment);
        comment.setUserId(UserUtil.getNowUser().getId());
        return save(comment);
    }

    @Override
    public boolean deleteComment(Long id) {
        LoginUser nowUser = UserUtil.getNowUser();
        if (nowUser.getRole().equals(UserRole.STUDENT) && !nowUser.getId().equals(getById(id).getUserId())) {
            throw new MessagePromptException("您不能删除其他用户的评论！");
        }
        return removeById(id);
    }

    @Override
    public boolean deleteCommentByCourseId(Long courseId) {
        LambdaUpdateWrapper<Comment> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Comment::getCourseId, courseId);
        return remove(wrapper);
    }
}
