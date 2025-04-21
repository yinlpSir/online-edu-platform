package com.jh.education.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.PhoneUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.jh.education.common.bean.LoginUser;
import com.jh.education.common.bean.MyPage;
import com.jh.education.common.constant.Authentication;
import com.jh.education.common.constant.UserRole;
import com.jh.education.common.exception.MessagePromptException;
import com.jh.education.common.util.COSUtil;
import com.jh.education.common.util.UserUtil;
import com.jh.education.feign.vo.TeacherInfo;
import com.jh.education.user.constant.DefaultValue;
import com.jh.education.user.dto.TeacherRegisterDTO;
import com.jh.education.user.dto.TeacherUpdateDTO;
import com.jh.education.user.entity.Teacher;
import com.jh.education.user.entity.User;
import com.jh.education.user.mapper.TeacherMapper;
import com.jh.education.user.service.TeacherService;
import com.jh.education.user.service.UserService;
import com.jh.education.user.vo.TeacherVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author jh
 * @since 2023-07-14
 */
@Service
@Slf4j
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {

    @Resource
    private UserService userService;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public boolean registerTeacher(TeacherRegisterDTO registerDTO) {
        // 检测该用户是否存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhoneNumber, registerDTO.getPhoneNumber());
        if (userService.getOne(wrapper) != null) {
            throw new MessagePromptException("该用户已注册");
        }

        // 验证手机号和验证码
        if (!Objects.equals(redisTemplate.opsForValue().get(registerDTO.getPhoneNumber()), registerDTO.getVerificationCode())) {
            throw new MessagePromptException("验证码错误");
        }

        String key;

        if (registerDTO.getHeadPortrait() != null) {
            // 上传头像
            String filename = registerDTO.getHeadPortrait().getOriginalFilename();
            key = "img/" + registerDTO.getUsername() + "." + FileUtil.extName(filename);
            COSUtil.uploadFile(key, registerDTO.getHeadPortrait());
        }

        // 上传教资照片
        String filename = registerDTO.getTeacherCertificateImg().getOriginalFilename();
        key = "teacherCertificate/" + registerDTO.getRealName() + "." + FileUtil.extName(filename);
        COSUtil.uploadFile(key, registerDTO.getTeacherCertificateImg());

        User user = new User();
        BeanUtil.copyProperties(registerDTO, user, "headPortrait", "password");
        user.setRole(UserRole.TEACHER);
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        if (registerDTO.getHeadPortrait() != null) {
            filename = registerDTO.getHeadPortrait().getOriginalFilename();
            user.setHeadPortrait("img/" + registerDTO.getUsername() + "." + FileUtil.extName(filename));
        } else {
            user.setHeadPortrait(DefaultValue.DEFAULT_HEADPORTRAIT_KEY);
        }
        userService.save(user);

        Teacher teacher = new Teacher();
        BeanUtil.copyProperties(registerDTO, teacher, "teacherCertificateImg");
        teacher.setId(user.getId());
        teacher.setTeacherCertificateImg(key);
        return save(teacher);
    }

    @Override
    public boolean updateTeacher(TeacherUpdateDTO updateDTO) {
        LoginUser nowUser = UserUtil.getNowUser();

        User user = new User();
        BeanUtil.copyProperties(updateDTO, user, "headPortrait");
        user.setId(nowUser.getId());

        Teacher teacher = new Teacher();
        BeanUtil.copyProperties(updateDTO, teacher, "teacherCertificateImg");
        teacher.setId(nowUser.getId());

        boolean f1 = false, f2 = false;

        if (!BeanUtil.isEmpty(user, "id")) {
            if (updateDTO.getHeadPortrait() != null) {
                // 获取原来的key
                String key = userService.getById(user.getId()).getHeadPortrait();
                // 默认头像
                if (DefaultValue.DEFAULT_HEADPORTRAIT_KEY.equals(key)) {
                    String username;
                    if (StringUtils.hasText(updateDTO.getUsername())) {
                        username = updateDTO.getUsername();
                    } else {
                        username = nowUser.getUsername();
                    }
                    key = "img/" + username + "." + FileUtil.extName(updateDTO.getHeadPortrait().getOriginalFilename());
                    user.setHeadPortrait(key);
                }
                // 上传头像到COS
                COSUtil.uploadFile(key, updateDTO.getHeadPortrait());
            }
            f1 = userService.updateById(user);
        }

        if (!BeanUtil.isEmpty(teacher, "id")) {
            teacher.setIsAuthenticated(Authentication.PENDING_REVIEW);
            if (updateDTO.getTeacherCertificateImg() != null) {
                // 上传教资到COS
                String key = getById(user.getId()).getTeacherCertificateImg();
                COSUtil.uploadFile(key, updateDTO.getTeacherCertificateImg());
            }
            f2 = updateById(teacher);
        }
        return f1 || f2;
    }

    @Override
    public void authenticateTeacher(Long id, Boolean isAuthenticated) {
        Teacher teacher = new Teacher();
        teacher.setId(id);
        teacher.setIsAuthenticated(isAuthenticated ? Authentication.PASSED : Authentication.FAILED);
        updateById(teacher);
    }

    @Override
    public Page<TeacherVO> getAllTeacher(MyPage myPage) {
        MPJLambdaWrapper<Teacher> wrapper = new MPJLambdaWrapper<>();
        wrapper
                .select(Teacher::getRealName, Teacher::getIsAuthenticated,Teacher::getTeacherCertificateImg)
                .innerJoin(User.class, User::getId, Teacher::getId)
                .select(User::getId, User::getHeadPortrait, User::getUsername, User::getPhoneNumber, User::getGender, User::getIntroduction);

        Page<TeacherVO> page = Page.of(myPage.getCurrentPage(), myPage.getPageSize());
        page.setOptimizeCountSql(false);
        page.setOptimizeJoinOfCountSql(false);
        Page<TeacherVO> result = baseMapper.selectJoinPage(page, TeacherVO.class, wrapper);

        result.getRecords().forEach(v -> {
            // 手机号脱敏
            v.setPhoneNumber(PhoneUtil.hideBetween(v.getPhoneNumber()).toString());
            // 获取头像的绝对路径
            v.setHeadPortrait(COSUtil.getFileUrl(v.getHeadPortrait()));
            // 获取教资的绝对路径
            v.setTeacherCertificateImg(COSUtil.getFileUrl(v.getTeacherCertificateImg()));
        });

        return result;
    }

    @Override
    public boolean removeById(Serializable id) {
        // 删除教资证
        COSUtil.deleteFile(getById(id).getTeacherCertificateImg());
        return super.removeById(id);
    }

    @Override
    public List<TeacherInfo> getTeacherInfoByTeacherName(String teacherName) {
        MPJLambdaWrapper<Teacher> wrapper = new MPJLambdaWrapper<>();
        wrapper
                .select(Teacher::getRealName)
                .innerJoin(User.class, User::getId, Teacher::getId)
                .select(User::getId, User::getHeadPortrait, User::getGender, User::getIntroduction)
                .like(Teacher::getRealName, teacherName);

        List<TeacherInfo> list = baseMapper.selectJoinList(TeacherInfo.class, wrapper);

        list.forEach(v -> v.setHeadPortrait(COSUtil.getFileUrl(v.getHeadPortrait())));

        return list;
    }

    @Override
    public TeacherInfo getOneTeacherInfo(Long id) {
        MPJLambdaWrapper<Teacher> wrapper = new MPJLambdaWrapper<>();
        wrapper
                .select(Teacher::getRealName, Teacher::getIsAuthenticated)
                .innerJoin(User.class, User::getId, Teacher::getId)
                .select(User::getId, User::getHeadPortrait, User::getGender, User::getIntroduction)
                .eq(User::getId, id);
        TeacherInfo teacherInfo = baseMapper.selectJoinOne(TeacherInfo.class, wrapper);
        teacherInfo.setHeadPortrait(COSUtil.getFileUrl(teacherInfo.getHeadPortrait()));
        return teacherInfo;
    }
}
