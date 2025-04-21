package com.jh.education.live.controller;

import com.jh.education.common.bean.CommonResult;
import com.jh.education.feign.vo.LiveVO;
import com.jh.education.live.service.LiveService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/live")
public class LiveController {
    @Resource
    private LiveService liveService;

    @PostMapping("/{classId}")
    @PreAuthorize("hasAnyAuthority('teacher')")
    public boolean addLive(@PathVariable Long classId) {
        return liveService.addLive(classId);
    }

    @DeleteMapping("/{classId}")
    @PreAuthorize("hasAnyAuthority('teacher')")
    public boolean deleteLive(@PathVariable Long classId) {
        return liveService.deleteLive(classId);
    }

    @GetMapping("/startLive/{classId}")
    @PreAuthorize("hasAnyAuthority('teacher')")
    public CommonResult startLive(@PathVariable Long classId) {
        return CommonResult.success("开始直播", liveService.startLive(classId));
    }

    @GetMapping("/{classId}")
    @PreAuthorize("isFullyAuthenticated()")
    public LiveVO getLiveVO(@PathVariable Long classId) {
        return liveService.getLiveVO(classId);
    }
}