package com.jh.education.course.classinfo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jh.education.course.classinfo.entity.ClassProcess;

public interface ClassProcessService extends IService<ClassProcess> {
    boolean saveClassProcess(Long classId, Long classProcess);

    Long getClassProcess(Long classId);

    boolean deleteClassProcess(Long classId);
}
