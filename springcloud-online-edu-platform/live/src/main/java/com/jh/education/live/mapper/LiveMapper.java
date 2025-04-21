package com.jh.education.live.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.jh.education.live.bean.Live;
import com.jh.education.live.bean.LiveInfo;

public interface LiveMapper extends MPJBaseMapper<Live> {
    LiveInfo getLiveInfo(Long classId);
}
