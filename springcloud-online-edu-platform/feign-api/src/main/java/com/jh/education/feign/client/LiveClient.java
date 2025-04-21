package com.jh.education.feign.client;

import com.jh.education.feign.config.FeignConfig;
import com.jh.education.feign.vo.LiveVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "liveService", path = "/live", configuration = {FeignConfig.class})
public interface LiveClient {
    @PostMapping("/{classId}")
    boolean addLive(@PathVariable Long classId);

    @DeleteMapping("/{classId}")
    boolean deleteLive(@PathVariable Long classId);

    @GetMapping("/{classId}")
    LiveVO getLiveVO(@PathVariable Long classId);
}
