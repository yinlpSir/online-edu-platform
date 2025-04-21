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
import com.jh.education.common.constant.UserRole;
import com.jh.education.common.exception.MessagePromptException;
import com.jh.education.common.util.COSUtil;
import com.jh.education.common.util.UserUtil;
import com.jh.education.user.constant.DefaultValue;
import com.jh.education.user.dto.StudentRegisterDTO;
import com.jh.education.user.dto.StudentUpdateDTO;
import com.jh.education.user.entity.Student;
import com.jh.education.user.entity.User;
import com.jh.education.user.mapper.StudentMapper;
import com.jh.education.user.service.StudentService;
import com.jh.education.user.service.UserService;
import com.jh.education.user.vo.StudentVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
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
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {

    @Resource
    private UserService userService;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public boolean registerStudent(StudentRegisterDTO registerDTO) {
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

        String key = DefaultValue.DEFAULT_HEADPORTRAIT_KEY;

        if (registerDTO.getHeadPortrait() != null) {
            String filename = registerDTO.getHeadPortrait().getOriginalFilename();
            key = "img/" + registerDTO.getUsername() + "." + FileUtil.extName(filename);
            // 上传头像到COS
            COSUtil.uploadFile(key, registerDTO.getHeadPortrait());
        }

        User user = new User();
        BeanUtil.copyProperties(registerDTO, user, "headPortrait", "password");
        user.setRole(UserRole.STUDENT);
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setHeadPortrait(key);
        userService.save(user);

        Student student = new Student();
        BeanUtil.copyProperties(registerDTO, student);
        student.setId(user.getId());

        return save(student);
    }

    @Override
    public Map<String, Object> updateStudent(StudentUpdateDTO updateDTO) {
        LoginUser nowUser = UserUtil.getNowUser();

        User user = new User();
        BeanUtil.copyProperties(updateDTO, user, "headPortrait");
        user.setId(nowUser.getId());

        Student student = new Student();
        BeanUtil.copyProperties(updateDTO, student);
        student.setId(nowUser.getId());

        if (updateDTO.getHeadPortrait() != null || !BeanUtil.isEmpty(user, "id")) {
            if (updateDTO.getHeadPortrait() != null) {
                String key = userService.getById(nowUser.getId()).getHeadPortrait();
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
                COSUtil.uploadFile(key, updateDTO.getHeadPortrait());
            }
            userService.updateById(user);
        }

        if (!BeanUtil.isEmpty(student, "id")) {
            updateById(student);
        }

        User u = userService.getById(nowUser.getId());
        Map<String, Object> result = new HashMap<>(6);
        result.put("id", u.getId());
        result.put("username", u.getUsername());
        result.put("gender", u.getGender());
        result.put("introduction", u.getIntroduction());
        result.put("headPortrait", COSUtil.getFileUrl(u.getHeadPortrait()));
        result.put("role", u.getRole());

        return result;
    }

    @Override
    public Page<StudentVO> getAllStudent(MyPage myPage, String phoneNumber) {
        MPJLambdaWrapper<Student> wrapper = new MPJLambdaWrapper<>();
        wrapper
                .select(Student::getGrade)
                .innerJoin(User.class, User::getId, Student::getId)
                .select(User::getId, User::getHeadPortrait, User::getUsername, User::getPhoneNumber, User::getIntroduction, User::getGender)
                .like(StringUtils.hasText(phoneNumber), User::getPhoneNumber, phoneNumber);

        Page<StudentVO> page = Page.of(myPage.getCurrentPage(), myPage.getPageSize());
        page.setOptimizeCountSql(false);
        page.setOptimizeJoinOfCountSql(false);
        Page<StudentVO> result = baseMapper.selectJoinPage(page, StudentVO.class, wrapper);

        result.getRecords().forEach(v -> {
            // 手机号脱敏
            v.setPhoneNumber(PhoneUtil.hideBetween(v.getPhoneNumber()).toString());
            // 获取头像的绝对路径
            v.setHeadPortrait(COSUtil.getFileUrl(v.getHeadPortrait()));
        });

        return result;
    }

    @Override
    public StudentVO getStudentDetail(Long id) {
        MPJLambdaWrapper<Student> wrapper = new MPJLambdaWrapper<>();
        wrapper
                .select(Student::getGrade)
                .innerJoin(User.class, User::getId, Student::getId)
                .select(User::getId, User::getHeadPortrait, User::getUsername, User::getPhoneNumber, User::getIntroduction, User::getGender)
                .eq(User::getId, id);
        StudentVO studentVO = baseMapper.selectJoinOne(StudentVO.class, wrapper);
        studentVO.setHeadPortrait(COSUtil.getFileUrl(studentVO.getHeadPortrait()));
        return studentVO;
    }
}
