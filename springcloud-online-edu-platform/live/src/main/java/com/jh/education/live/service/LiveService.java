package com.jh.education.live.service;

import com.jh.education.feign.vo.LiveVO;
import com.jh.education.live.bean.LiveInfo;

import java.util.Map;

public interface LiveService {
    boolean addLive(Long classId);

    LiveInfo getLiveInfo(Long classId);

    String startLive(Long classId);

    boolean deleteLive(Long classId);

    // 获取直播基本信息
    LiveVO getLiveVO(Long classId);
}
