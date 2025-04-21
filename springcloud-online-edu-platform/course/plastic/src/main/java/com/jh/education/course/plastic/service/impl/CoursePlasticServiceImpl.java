package com.jh.education.course.plastic.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jh.education.common.constant.UserRole;
import com.jh.education.common.util.COSUtil;
import com.jh.education.common.util.UserUtil;
import com.jh.education.course.plastic.dto.CoursePlasticAddDTO;
import com.jh.education.course.plastic.dto.CoursePlasticUpdateDTO;
import com.jh.education.course.plastic.entity.CoursePlastic;
import com.jh.education.course.plastic.mapper.CoursePlasticMapper;
import com.jh.education.course.plastic.service.CoursePlasticService;
import com.jh.education.feign.client.CourseClient;
import com.jh.education.feign.vo.CoursePlasticVO;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
public class CoursePlasticServiceImpl extends ServiceImpl<CoursePlasticMapper, CoursePlastic> implements CoursePlasticService {

    @Resource
    private CourseClient courseClient;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Override
    public boolean updateCoursePlastic(CoursePlasticUpdateDTO dto) {
        CoursePlastic coursePlastic = new CoursePlastic();
        coursePlastic.setId(dto.getId());
        if (StringUtils.hasText(dto.getPlasticName())) {
            coursePlastic.setPlasticName(dto.getPlasticName());
        }
        if (dto.getPlastic() != null) {
            String key = getById(dto.getId()).getPlasticPath();
            COSUtil.uploadFile(key, dto.getPlastic());
            coursePlastic.setPlasticSize(dto.getPlastic().getSize());
        }
        return updateById(coursePlastic);
    }

    @Override
    public boolean deleteCoursePlastic(Long id) {
        COSUtil.deleteFile(getById(id).getPlasticPath());
        return removeById(id);
    }

    @Override
    public boolean addCoursePlastic(CoursePlasticAddDTO dto) {
        rabbitTemplate.convertAndSend("initCourseAuthenticationStatus", dto.getCourseId());
        CoursePlastic coursePlastic = new CoursePlastic();
        coursePlastic.setPlasticName(dto.getPlasticName());
        String key = "coursePlastic/" + courseClient.getCourseNameById(dto.getCourseId()) + "/" + dto.getPlasticName() + "." + FileUtil.extName(dto.getPlastic().getOriginalFilename());
        COSUtil.uploadFile(key, dto.getPlastic());
        coursePlastic.setPlasticPath(key);
        coursePlastic.setPlasticSize(dto.getPlastic().getSize());
        coursePlastic.setCourseId(dto.getCourseId());
        return save(coursePlastic);
    }

    @Override
    public List<CoursePlasticVO> getCoursePlasticByCourseId(Long courseId) {
        try {
            if (UserUtil.getNowUser().getRole().equals(UserRole.STUDENT) && !courseClient.isFreeCourse(courseId) && !courseClient.studentIsBougutCourse(courseId)) {
                return null;
            }
        } catch (ClassCastException e) {
            return null;
        }

        LambdaQueryWrapper<CoursePlastic> wrapper = new LambdaQueryWrapper<>();
        wrapper
                .select(CoursePlastic::getId, CoursePlastic::getPlasticName, CoursePlastic::getPlasticPath, CoursePlastic::getPlasticSize)
                .eq(CoursePlastic::getCourseId, courseId);
        List<CoursePlastic> coursePlastics = baseMapper.selectList(wrapper);
        List<CoursePlasticVO> list = new ArrayList<>();
        coursePlastics.forEach(c -> {
            c.setPlasticPath(COSUtil.getFileUrl(c.getPlasticPath()));
            CoursePlasticVO vo = new CoursePlasticVO();
            BeanUtil.copyProperties(c, vo);
            if (UserUtil.getNowUser().getRole().equals(UserRole.STUDENT)) {
                vo.setId(null);
            }
            list.add(vo);
        });
        return list;
    }

    @Override
    public boolean deleteCoursePlasticByCourse(Long courseId) {
        LambdaQueryWrapper<CoursePlastic> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CoursePlastic::getCourseId, courseId);
        baseMapper.selectList(wrapper).forEach(x -> deleteCoursePlastic(x.getId()));
        return true;
    }
}