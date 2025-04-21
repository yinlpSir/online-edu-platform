package com.jh.education.course.classinfo.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jh.education.common.bean.LoginUser;
import com.jh.education.common.constant.UserRole;
import com.jh.education.common.exception.MessagePromptException;
import com.jh.education.common.util.COSUtil;
import com.jh.education.common.util.UserUtil;
import com.jh.education.course.classinfo.dto.ClassAddDTO;
import com.jh.education.course.classinfo.dto.ClassUpdateDTO;
import com.jh.education.course.classinfo.entity.Class;
import com.jh.education.course.classinfo.mapper.ClassMapper;
import com.jh.education.course.classinfo.service.ClassProcessService;
import com.jh.education.course.classinfo.service.ClassService;
import com.jh.education.feign.client.CourseClient;
import com.jh.education.feign.client.LiveClient;
import com.jh.education.feign.vo.ClassVO;
import com.jh.education.feign.vo.LiveVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ws.schild.jave.MultimediaInfo;
import ws.schild.jave.MultimediaObject;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
@Slf4j
public class ClassServiceImpl extends ServiceImpl<ClassMapper, Class> implements ClassService {

    @Resource
    private CourseClient courseClient;

    @Resource
    private LiveClient liveClient;

    @Resource
    private ClassProcessService classProcessService;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Override
    public LocalDateTime getCourseStartTime(Long courseId) {
        LambdaQueryWrapper<Class> wrapper = new LambdaQueryWrapper<>();
        wrapper
                .select(Class::getStartTime)
                .eq(Class::getCourseId, courseId)
                .eq(Class::getNumber, 1);
        Class one = getOne(wrapper);
        return one == null ? null : one.getStartTime();
    }

    @Override
    public List<ClassVO> getAllClassByCourseId(Long courseId) {
        LambdaQueryWrapper<Class> wrapper = new LambdaQueryWrapper<>();
        wrapper
                .eq(Class::getCourseId, courseId)
                .orderByAsc(Class::getNumber);
        List<Class> classes = baseMapper.selectList(wrapper);
        List<ClassVO> classList = new ArrayList<>();
        classes.forEach(c -> {
            ClassVO vo = new ClassVO();
            BeanUtil.copyProperties(c, vo, "lastTime");
            Long lastTime = c.getLastTime();
            if (lastTime != null) {
                vo.setLastTime(DateUtil.formatBetween(lastTime, BetweenFormatter.Level.SECOND));
            }
            classList.add(vo);
        });
        try {
            LoginUser nowUser = UserUtil.getNowUser();
            if (nowUser.getRole().equals(UserRole.TEACHER) || nowUser.getRole().equals(UserRole.ADMIN) || courseClient.isFreeCourse(courseId) || courseClient.studentIsBougutCourse(courseId)) {
                classList.forEach(v -> {
                    // 获取视频播放路径
                    if (StringUtils.hasText(v.getVideoPath())) {
                        v.setVideoPath(COSUtil.getFileUrl(v.getVideoPath()));
                    } else {
                        // 获取直播相关数据
                        LiveVO liveInfo = liveClient.getLiveVO(Long.parseLong(v.getId()));
                        v.setLiveInfo(liveInfo);
                    }
                    v.setClassProcess(classProcessService.getClassProcess(Long.parseLong(v.getId())));
                });
            }
        } catch (ClassCastException e) {
            // 匿名用户
        }
        return classList;
    }

    @Override
    public boolean addClass(ClassAddDTO dto) {
        LambdaQueryWrapper<Class> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Class::getCourseId, dto.getCourseId());
        List<Class> classList = list(wrapper);
        if (classList.stream().map(Class::getNumber).anyMatch(dto.getNumber()::equals)) {
            throw new MessagePromptException("课程序号重复");
        }

        rabbitTemplate.convertAndSend("initCourseAuthenticationStatus", dto.getCourseId());

        courseClient.setCourseNumber(dto.getCourseId(), classList.size() + 1);

        Class clazz = new Class();
        BeanUtil.copyProperties(dto, clazz);
        boolean save;
        // 视频处理
        if (dto.getVideo() != null) {
            String key = "classVideo/" + courseClient.getCourseNameById(clazz.getCourseId()) + "/" + clazz.getClassName() + "." + FileUtil.extName(dto.getVideo().getOriginalFilename());
            COSUtil.uploadFile(key, dto.getVideo());
            clazz.setVideoPath(key);
            // 获取视频时长
            Long lastTime = getVideoLastTime(key);
            clazz.setLastTime(lastTime);
            save = save(clazz);
        } else {
            if (dto.getStartTime() == null || dto.getEndTime() == null) {
                throw new MessagePromptException("课程开始时间和结束时间不能为空");
            }
            if (dto.getStartTime().isBefore(LocalDateTime.now())) {
                throw new MessagePromptException("课程开始时间不能早于当前时间");
            }
            if (dto.getEndTime().isBefore(dto.getStartTime())) {
                throw new MessagePromptException("课程结束时间不能早于开始时间");
            }

            save = save(clazz);

            liveClient.addLive(clazz.getId());
        }
        return save;
    }

    private static Long getVideoLastTime(String key) {
        try {
            String fileUrl = COSUtil.getFileUrl(key);
            MultimediaObject instance = new MultimediaObject(new URL(fileUrl));
            MultimediaInfo result = instance.getInfo();
            return result.getDuration();
        } catch (Exception e) {
            return 0L;
        }
    }

    @Override
    public boolean saveClassProcess(Long id, Long classProcess) {
        Long lastTime = getById(id).getLastTime();
        if (lastTime == null || classProcess >= lastTime / 1000 + 1) {
            classProcess = 0L;
        }
        return classProcessService.saveClassProcess(id, classProcess);
    }

    @Override
    public boolean deleteClassByCourseId(Long courseId) {
        LambdaQueryWrapper<Class> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Class::getCourseId, courseId);
        List<Class> classes = baseMapper.selectList(wrapper);
        if (classes == null) {
            return true;
        }
        classes.forEach(x -> {
            deleteClass(x.getId());
            classProcessService.deleteClassProcess(x.getId());
        });
        return true;
    }

    @Override
    public boolean deleteClass(Long id) {
        Class clazz = getById(id);
        // 删除视频
        String videoPath = clazz.getVideoPath();
        if (StringUtils.hasText(videoPath)) {
            COSUtil.deleteFile(videoPath);
        } else {
            liveClient.deleteLive(id);
        }
        Long courseId = clazz.getCourseId();
        LambdaQueryWrapper<Class> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Class::getCourseId, courseId);
        courseClient.setCourseNumber(courseId, (int) (count(wrapper)) - 1);
        return removeById(id);
    }

    @Override
    public boolean updateClass(ClassUpdateDTO dto) {
        Class clazz = new Class();
        BeanUtil.copyProperties(dto, clazz);
        // 视频处理
        if (dto.getVideo() != null) {
            Class realClass = getById(clazz.getId());
            String key = "classVideo/" + courseClient.getCourseNameById(realClass.getCourseId()) + "/" + realClass.getClassName() + "." + FileUtil.extName(dto.getVideo().getOriginalFilename());
            COSUtil.uploadFile(key, dto.getVideo());
            clazz.setVideoPath(key);
            Long lastTime = getVideoLastTime(key);
            clazz.setLastTime(lastTime);
        }
        if (BeanUtil.isEmpty(clazz, "id")) {
            return true;
        }
        return updateById(clazz);
    }
}
