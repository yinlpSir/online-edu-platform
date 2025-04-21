package com.jh.education.course.classinfo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jh.education.common.util.UserUtil;
import com.jh.education.course.classinfo.entity.ClassProcess;
import com.jh.education.course.classinfo.mapper.ClassProcessMapper;
import com.jh.education.course.classinfo.service.ClassProcessService;
import org.springframework.stereotype.Service;

@Service
public class ClassProcessServiceImpl extends ServiceImpl<ClassProcessMapper, ClassProcess> implements ClassProcessService {
    @Override
    public boolean saveClassProcess(Long classId, Long classProcess) {
        ClassProcess process = new ClassProcess();
        process.setClassId(classId);
        process.setClassProcess(classProcess);
        process.setStudentId(UserUtil.getNowUser().getId());
        LambdaQueryWrapper<ClassProcess> wrapper = new LambdaQueryWrapper<>();
        wrapper
                .select(ClassProcess::getId)
                .eq(ClassProcess::getClassId, classId);
        ClassProcess one = getOne(wrapper);
        if (one != null) {
            process.setId(one.getId());
            return updateById(process);
        }
        return save(process);
    }

    @Override
    public Long getClassProcess(Long classId) {
        LambdaQueryWrapper<ClassProcess> wrapper = new LambdaQueryWrapper<>();
        wrapper
                .select(ClassProcess::getClassProcess)
                .eq(ClassProcess::getClassId, classId)
                .eq(ClassProcess::getStudentId, UserUtil.getNowUser().getId());
        ClassProcess one = getOne(wrapper);
        return one == null ? 0 : one.getClassProcess();
    }

    @Override
    public boolean deleteClassProcess(Long classId) {
        LambdaUpdateWrapper<ClassProcess> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ClassProcess::getClassId, classId);
        return remove(wrapper);
    }
}
