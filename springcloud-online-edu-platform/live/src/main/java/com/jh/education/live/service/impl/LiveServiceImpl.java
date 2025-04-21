package com.jh.education.live.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jh.education.common.constant.UserRole;
import com.jh.education.common.exception.MessagePromptException;
import com.jh.education.common.util.UserUtil;
import com.jh.education.feign.vo.LiveVO;
import com.jh.education.live.bean.Live;
import com.jh.education.live.bean.LiveInfo;
import com.jh.education.live.mapper.LiveMapper;
import com.jh.education.live.sdk.MTCloud;
import com.jh.education.live.service.LiveService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class LiveServiceImpl extends ServiceImpl<LiveMapper, Live> implements LiveService {
    @Resource
    private MTCloud mtCloudClient;

    @Resource
    private LiveMapper liveMapper;

    @Override
    public boolean addLive(Long classId) {
        try {
            LiveInfo liveInfo = getLiveInfo(classId);
            HashMap<Object, Object> options = new HashMap<>(1);
            // 直播类型。1: 教育直播，2: 生活直播。默认 1。说明：根据平台开通的直播类型填写
            options.put("scenes", 2);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String res = mtCloudClient.courseAdd(liveInfo.getClassName(), liveInfo.getTeacherId().toString(), liveInfo.getStartTime().format(formatter), liveInfo.getEndTime().format(formatter), liveInfo.getTeacherName(), liveInfo.getTeacherIntroduction(), options);
            log.info(res);
            Map<String, Object> map = JSONUtil.toBean(res, Map.class);
            JSONObject jsonObject = (JSONObject) map.get("data");
            String courseId = (String) jsonObject.get("course_id");
            Live live = new Live();
            live.setClassId(classId);
            live.setCourseId(Integer.parseInt(courseId));
            return save(live);
        } catch (Exception e) {
//            throw new RuntimeException(e);
            log.info(e.getMessage());
            return false;
        }
    }

    @Override
    public LiveInfo getLiveInfo(Long classId) {
        return liveMapper.getLiveInfo(classId);
    }

    @Override
    public String startLive(Long classId) {
        Live live = getLiveByClassId(classId);
        if (StringUtils.hasText(live.getLiveUrl())) {
            return live.getLiveUrl();
        }
        LiveInfo liveInfo = getLiveInfo(classId);
        if (liveInfo.getStartTime().isAfter(LocalDateTime.now())) {
            throw new MessagePromptException("未到上课时间");
        }
        try {
            String res = mtCloudClient.courseLaunch(live.getCourseId());
            log.info(res);
            Map<String, Object> map = JSONUtil.toBean(res, Map.class);
            JSONObject jsonObject = (JSONObject) map.get("data");
            String url = (String) jsonObject.get("url");
            live.setLiveUrl(url);
            updateById(live);
            return url;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteLive(Long classId) {
        try {
            Live live = getLiveByClassId(classId);
            String res = mtCloudClient.courseDelete(live.getCourseId().toString());
            log.info(res);
            Map<String, Integer> map = JSONUtil.toBean(res, Map.class);
            Integer code = map.get("code");
            return Integer.valueOf(0).equals(code) && removeById(live);
        } catch (Exception e) {
//            throw new RuntimeException(e);
            log.info(e.getMessage());
            return false;
        }
    }

    @Override
    public LiveVO getLiveVO(Long classId) {
        Live live = getLiveByClassId(classId);
        if (live == null) {
            return null;
        }
        try {
            String res = mtCloudClient.courseGet(live.getCourseId().toString(), null);
            log.info(res);
            Map<String, Object> map = JSONUtil.toBean(res, Map.class);
            JSONObject jsonObject = (JSONObject) map.get("data");
            LiveVO liveVO = new LiveVO();
            BeanUtil.copyProperties(jsonObject, liveVO);
            if (UserUtil.getNowUser().getRole().equals(UserRole.TEACHER)) {
                liveVO.setLiveUrl(live.getLiveUrl());
            }
            if (liveVO.getLiveStatus().equals(3) && StringUtils.hasText(live.getLiveUrl())) {
                LambdaUpdateWrapper<Live> wrapper = new LambdaUpdateWrapper<>();
                wrapper
                        .eq(Live::getId, live.getId())
                        .set(Live::getLiveUrl, null);
                update(wrapper);
            }
            return liveVO;
        } catch (Exception e) {
//            throw new RuntimeException(e);
            log.info(e.getMessage());
            return null;
        }
    }

    private Live getLiveByClassId(Long classId) {
        LambdaQueryWrapper<Live> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Live::getClassId, classId);
        return getOne(wrapper);
    }
}
