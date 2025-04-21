package com.jh.education.course.info.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.jh.education.common.bean.LoginUser;
import com.jh.education.common.constant.Authentication;
import com.jh.education.common.constant.UserRole;
import com.jh.education.common.exception.MessagePromptException;
import com.jh.education.common.util.COSUtil;
import com.jh.education.common.util.UserUtil;
import com.jh.education.course.info.dto.CourseAddDTO;
import com.jh.education.course.info.dto.CourseManagementDTO;
import com.jh.education.course.info.dto.CourseQueryDTO;
import com.jh.education.course.info.dto.CourseUpdateDTO;
import com.jh.education.course.info.entity.Course;
import com.jh.education.course.info.entity.StudentBuyCourse;
import com.jh.education.course.info.mapper.CourseMapper;
import com.jh.education.course.info.properties.AlipayProperties;
import com.jh.education.course.info.service.CommentService;
import com.jh.education.course.info.service.CourseService;
import com.jh.education.course.info.service.StudentBuyCourseService;
import com.jh.education.course.info.util.CourseUtil;
import com.jh.education.course.info.vo.CourseDetailVO;
import com.jh.education.course.info.vo.CourseVO;
import com.jh.education.feign.client.ClassClient;
import com.jh.education.feign.client.CoursePlasticClient;
import com.jh.education.feign.client.UserClient;
import com.jh.education.feign.vo.ClassVO;
import com.jh.education.feign.vo.CoursePlasticVO;
import com.jh.education.feign.vo.TeacherInfo;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author jh
 * @since 2023-07-19
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    @Resource
    private CommentService commentService;

    @Resource
    private StudentBuyCourseService studentBuyCourseService;

    @Resource
    private UserClient userClient;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private ClassClient classClient;

    @Resource
    private RedisTemplate<String, Long> redisTemplate;

    @Resource
    private CoursePlasticClient coursePlasticClient;

    @Resource
    private AlipayProperties alipayProperties;

    @Override
    public Page<CourseVO> getAllCourse(CourseQueryDTO queryDTO) {
        MPJLambdaWrapper<Course> wrapper = new MPJLambdaWrapper<>();
        wrapper
                .select(Course::getId, Course::getCourseName, Course::getCourseCover, Course::getPrice, Course::getGrade, Course::getNumber, Course::getSubject, Course::getPurchaseQuantity, Course::getTeacherId)
                .like(StringUtils.hasText(queryDTO.getCourseName()), Course::getCourseName, queryDTO.getCourseName())
                .eq(Course::getIsPassed, Authentication.PASSED)
                .eq(StringUtils.hasText(queryDTO.getSubject()), Course::getSubject, queryDTO.getSubject())
                .eq(queryDTO.getCourseType() != null, Course::getCourseType, queryDTO.getCourseType());

        if (StringUtils.hasText(queryDTO.getGrade())) {
            wrapper.apply("FIND_IN_SET({0}, grade) > 0", CourseUtil.getGrade(queryDTO.getGrade()));
        }

        if (StringUtils.hasText(queryDTO.getTeacherName())) {
            List<TeacherInfo> teacherList = userClient.getByTeacherName(queryDTO.getTeacherName());
            List<Long> collect = teacherList.stream().map(TeacherInfo::getId).collect(Collectors.toList());
            // 如果没有该教师的课
            boolean collectEmpty = collect.isEmpty();
            if (collectEmpty) {
                wrapper.apply("1=0");
            } else {
                wrapper.in(Course::getTeacherId, collect);
            }
        }

        Page<CourseVO> page = baseMapper.selectJoinPage(Page.of(queryDTO.getCurrentPage(), queryDTO.getPageSize()), CourseVO.class, wrapper);

        page.getRecords().forEach(v -> {
            TeacherInfo teacherInfo = userClient.getOneTeacherInfo(v.getTeacherId());
            v.setTeacherName(teacherInfo.getRealName());
            v.setTeacherHeadPortrait(teacherInfo.getHeadPortrait());

            v.setStartTime(classClient.getCourseStartTime(Long.parseLong(v.getId())));

            v.setCourseCover(COSUtil.getFileUrl(v.getCourseCover()));
            try {
                if (UserUtil.getNowUser().getRole().equals(UserRole.STUDENT) && studentBuyCourseService.studentIsBougutCourse(Long.parseLong(v.getId()))) {
                    v.setIsBought(true);
                } else {
                    v.setIsBought(false);
                }
            } catch (ClassCastException e) {
                v.setIsBought(false);
            }
        });

        return page;
    }

    @Override
    public CourseDetailVO getCourseDetail(Long id) {
        CourseDetailVO courseDetail = new CourseDetailVO();

        Course course = getById(id);
        course.setCourseCover(COSUtil.getFileUrl(course.getCourseCover()));
        BeanUtil.copyProperties(course, courseDetail);

        // 查询课程评论
        courseDetail.setCourseComment(commentService.getCommentByCourseId(course.getId()));

        // 获取课程详情
        List<ClassVO> classList = classClient.getAllClassByCourseId(course.getId());
        try {
            // 未购买不能查看视频
            if (UserUtil.getNowUser().getRole().equals(UserRole.STUDENT) && !isFreeCourse(course.getId()) && !studentBuyCourseService.studentIsBougutCourse(course.getId())) {
                classList.forEach(v -> {
                    v.setId(null);
                    v.setVideoPath(null);
                    v.setLastTime(null);
                    v.setClassProcess(null);
                });
            }
        } catch (ClassCastException e) {
            classList.forEach(v -> {
                v.setId(null);
                v.setVideoPath(null);
                v.setLastTime(null);
                v.setClassProcess(null);
            });
        }

        courseDetail.setClassList(classList);

        // 获取教师信息
        TeacherInfo teacherInfo = userClient.getOneTeacherInfo(course.getTeacherId());
        try {
            if (UserUtil.getNowUser().getRole().equals(UserRole.STUDENT)) {
                teacherInfo.setId(null);
            }
        } catch (ClassCastException e) {
            teacherInfo.setId(null);
        }
        courseDetail.setTeacherInfo(teacherInfo);

        List<CoursePlasticVO> coursePlastic = coursePlasticClient.getCoursePlasticByCourseId(course.getId());
        courseDetail.setCoursePlastic(coursePlastic);

        return courseDetail;
    }

    @Override
    public boolean authenticateCourse(Long id, Boolean isPassed) {
        Course course = new Course();
        course.setId(id);
        course.setIsPassed(isPassed ? Authentication.PASSED : Authentication.FAILED);
        return updateById(course);
    }

    @Override
    public Long addCourse(CourseAddDTO dto) {
        LoginUser nowUser = UserUtil.getNowUser();

        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getCourseName, dto.getCourseName());
        if (count(wrapper) != 0) {
            throw new MessagePromptException("课程名称不能重复");
        }

        Course course = new Course();
        BeanUtil.copyProperties(dto, course, "courseCover", "grade");

        String[] grade = dto.getGrade();
        if (grade != null && grade.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (String v : grade) {
                sb.append(v).append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            course.setGrade(sb.toString());
        }

        course.setTeacherId(nowUser.getId());

        if (dto.getCourseCover() != null) {
            String key = "courseCover/" + dto.getCourseName() + "." + FileUtil.extName(dto.getCourseCover().getOriginalFilename());
            COSUtil.uploadFile(key, dto.getCourseCover());
            course.setCourseCover(key);
        }

        save(course);

        return course.getId();
    }

    @Override
    public boolean updateCourse(CourseUpdateDTO dto) {
        LoginUser nowUser = UserUtil.getNowUser();
        if (!nowUser.getRole().equals(UserRole.ADMIN) && dto.getCourseType() != null) {
            throw new MessagePromptException("您不能修改课程类别！");
        }

        Course course = getById(dto.getId());
        if (!nowUser.getId().equals(course.getTeacherId())) {
            throw new MessagePromptException("您不能修改其他老师的课程！");
        }

        Course updateCourse = new Course();
        BeanUtil.copyProperties(dto, updateCourse, "courseCover", "grade");

        String[] grade = dto.getGrade();
        if (grade != null && grade.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (String v : grade) {
                sb.append(v).append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            updateCourse.setGrade(sb.toString());
        }

        if (dto.getCourseCover() != null) {
            String key = course.getCourseCover();
            if (!StringUtils.hasText(key)) {
                key = "courseCover/" + course.getCourseName() + "." + FileUtil.extName(dto.getCourseCover().getOriginalFilename());
                updateCourse.setCourseCover(key);
            }
            COSUtil.uploadFile(key, dto.getCourseCover());
        }

        if (BeanUtil.isEmpty(updateCourse, "id")) {
            return true;
        }
        return updateById(updateCourse);
    }

    @Override
    public String buyCourse(Long id) throws AlipayApiException {
        // 验证学生是否购买
        if (studentBuyCourseService.studentIsBougutCourse(id)) {
            throw new MessagePromptException("您已购买此课程，请前往个人中心学习！");
        }

        Course course = getById(id);

        // 判断课程是否审核通过
        if (!course.getIsPassed().equals(Authentication.PASSED)) {
            throw new MessagePromptException("该课程未审核通过！");
        }

        if (isFreeCourse(id)) {
            studentBuyCourseService.addBuyCourseRecord(id, UserUtil.getNowUser().getId());
            Course updateCourse = new Course();
            updateCourse.setId(id);
            // 购买人数 + 1
            updateCourse.setPurchaseQuantity(course.getPurchaseQuantity() + 1);
            updateById(updateCourse);
            throw new MessagePromptException("此课程免费，无需购买，请直接前往个人中心学习！");
        }

        // 调用订单服务生成订单
        String orderNo = RandomUtil.randomNumbers(20);
        String orderSubject = String.format("ID为%d的课程", id);

        Map<String, Object> map = new HashMap<>(3);
        map.put("orderNo", orderNo);
        map.put("orderSubject", orderSubject);
        map.put("orderPrice", course.getPrice());
        map.put("userId", UserUtil.getNowUser().getId());

        rabbitTemplate.convertAndSend("orderExchange", "generate", map);

        // 调用支付宝的支付接口进行课程购买
        AlipayConfig alipayConfig = new AlipayConfig();
        alipayConfig.setServerUrl(alipayProperties.getGatewayUrl());
        alipayConfig.setAppId(alipayProperties.getAppId());
        alipayConfig.setPrivateKey(alipayProperties.getPrivateKey());
        alipayConfig.setFormat("json");
        alipayConfig.setAlipayPublicKey(alipayProperties.getAlipayPublicKey());
        alipayConfig.setCharset("UTF8");
        alipayConfig.setSignType("RSA2");
        AlipayClient alipayClient = new DefaultAlipayClient(alipayConfig);
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        AlipayTradePagePayModel model = new AlipayTradePagePayModel();
        model.setOutTradeNo(orderNo);
        model.setTotalAmount(course.getPrice().toPlainString());
        model.setSubject(orderSubject);
        model.setProductCode("FAST_INSTANT_TRADE_PAY");
        // 设置订单的绝对超时时间
        model.setTimeExpire(DateUtil.format(new Date(System.currentTimeMillis() + 30 * 60 * 1000), "yyyy-MM-dd HH:mm:ss"));
        request.setBizModel(model);
        request.setReturnUrl(alipayProperties.getReturnUrl());
        request.setNotifyUrl(alipayProperties.getNotifyUrl() + "/course/info/buyCourseIsSuccessful");

        redisTemplate.opsForValue().set("订单号为：" + orderNo + "的用户", UserUtil.getNowUser().getId(), 30, TimeUnit.MINUTES);

        AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
        if (response.isSuccess()) {
            return response.getBody();
        } else {
            throw new MessagePromptException("调用支付宝接口失败");
            // sdk版本是"4.38.0.ALL"及以上,可以参考下面的示例获取诊断链接
            // String diagnosisUrl = DiagnosisUtils.getDiagnosisUrl(response);
            // System.out.println(diagnosisUrl);
        }
    }

    @Override
    public boolean buyCourseIsSuccessful(Map<String, String[]> result) throws AlipayApiException {
        // 获取支付宝POST过来反馈信息，将异步通知中收到的待验证所有参数都存放到map中
        Map<String, String> params = new HashMap<>();
        for (String name : result.keySet()) {
            String[] values = result.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }
        // 调用SDK验证签名
        boolean signVerified = AlipaySignature.rsaCheckV1(params, alipayProperties.getAlipayPublicKey(), "UTF8", "RSA2");

        // 验签失败
        if (!signVerified) {
            throw new MessagePromptException("验签失败");
        }

        String tradeStatus = result.get("trade_status")[0];
        // 支付失败
        if (!"TRADE_SUCCESS".equals(tradeStatus)) {
            throw new MessagePromptException("支付失败");
        }

        // 修改订单状态
        Map<String, Object> map = new HashMap<>(3);
        String orderNo = result.get("out_trade_no")[0];
        map.put("orderNo", orderNo);
        map.put("orderStatus", 1);
        rabbitTemplate.convertAndSend("orderExchange", "update", map);

        // 获取课程ID
        String orderSubject = result.get("subject")[0];
        Long id = Long.valueOf(orderSubject.substring(3, orderSubject.indexOf("的")));

        Course course = getById(id);
        Course updateCourse = new Course();
        updateCourse.setId(id);
        // 购买人数 + 1
        updateCourse.setPurchaseQuantity(course.getPurchaseQuantity() + 1);

        return studentBuyCourseService.addBuyCourseRecord(id, redisTemplate.opsForValue().get("订单号为：" + orderNo + "的用户")) && updateById(updateCourse);
    }

    @Override
    public boolean deleteCourse(Long id) {
        // 删除课程封面
        String courseCover = getById(id).getCourseCover();
        if (StringUtils.hasText(courseCover)) {
            COSUtil.deleteFile(courseCover);
        }
        return removeById(id) && studentBuyCourseService.deleteBuyCourseRecordByCourseId(id);
    }

    @Override
    public String getCourseNameById(Long id) {
        return getById(id).getCourseName();
    }

    @Override
    public boolean isFreeCourse(Long id) {
        return getById(id).getPrice().compareTo(BigDecimal.ZERO) == 0;
    }

    @Override
    public Page<CourseVO> getAllBoughtCourse(CourseQueryDTO queryDTO) {
        MPJLambdaWrapper<Course> wrapper = new MPJLambdaWrapper<>();
        LoginUser nowUser = UserUtil.getNowUser();
        wrapper
                .select(Course::getId, Course::getCourseCover, Course::getCourseName, Course::getPrice, Course::getGrade, Course::getNumber, Course::getSubject, Course::getPurchaseQuantity, Course::getTeacherId)
                .like(StringUtils.hasText(queryDTO.getCourseName()), Course::getCourseName, queryDTO.getCourseName())
                .eq(StringUtils.hasText(queryDTO.getGrade()), Course::getGrade, queryDTO.getGrade());

        if (StringUtils.hasText(queryDTO.getGrade())) {
            wrapper.apply("FIND_IN_SET({0}, grade) > 0", CourseUtil.getGrade(queryDTO.getGrade()));
        }

        if (StringUtils.hasText(queryDTO.getTeacherName())) {
            List<TeacherInfo> teacherList = userClient.getByTeacherName(queryDTO.getTeacherName());
            List<Long> collect = teacherList.stream().map(TeacherInfo::getId).collect(Collectors.toList());
            wrapper.in(Course::getTeacherId, collect);
        }

        Page<CourseVO> page = Page.of(queryDTO.getCurrentPage(), queryDTO.getPageSize());
        page.setOptimizeCountSql(false);
        page.setOptimizeJoinOfCountSql(false);

        wrapper
                .innerJoin(StudentBuyCourse.class, StudentBuyCourse::getCourseId, Course::getId)
                .eq(StudentBuyCourse::getStudentId, nowUser.getId());

        Page<CourseVO> result = baseMapper.selectJoinPage(page, CourseVO.class, wrapper);

        result.getRecords().forEach(v -> {
            TeacherInfo teacherInfo = userClient.getOneTeacherInfo(v.getTeacherId());
            v.setTeacherId(null);
            v.setTeacherName(teacherInfo.getRealName());
            v.setTeacherHeadPortrait(teacherInfo.getHeadPortrait());

            v.setStartTime(classClient.getCourseStartTime(Long.parseLong(v.getId())));

            v.setCourseCover(COSUtil.getFileUrl(v.getCourseCover()));

            v.setIsBought(true);
        });

        return result;
    }

    @Override
    public List<Map> getBanner() {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper
                .select(Course::getId, Course::getCourseCover)
                .eq(Course::getCourseType, 1)
                .eq(Course::getIsPassed, Authentication.PASSED);
        List<Course> courses = baseMapper.selectList(wrapper);
        List<Map> list = new ArrayList<>();
        courses.forEach(x -> {
            Map map = new HashMap<>();
            map.put("id", x.getId().toString());
            map.put("url", COSUtil.getFileUrl(x.getCourseCover()));
            list.add(map);
        });
        return list;
    }

    @Override
    public Page<Map> courseManagement(CourseManagementDTO dto) {
        LoginUser nowUser = UserUtil.getNowUser();
        Long teacherId = nowUser.getId();
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper
                .select(Course::getId, Course::getCourseName, Course::getCourseCover, Course::getDescription, Course::getPrice, Course::getGrade, Course::getNumber, Course::getSubject, Course::getIsPassed, Course::getTeacherId)
                .eq(nowUser.getRole().equals(UserRole.TEACHER), Course::getTeacherId, teacherId)
                .like(StringUtils.hasText(dto.getCourseName()), Course::getCourseName, dto.getCourseName())
                .eq(dto.getIsPassed() != null, Course::getIsPassed, dto.getIsPassed());
        Page<Course> page = baseMapper.selectPage(Page.of(dto.getCurrentPage(), dto.getPageSize()), wrapper);
        Page<Map> mapPage = new Page<>();
        BeanUtil.copyProperties(page, mapPage, "records");
        List<Map> records = new ArrayList<>();
        List<String> ignoreProperties = new ArrayList<>();
        ignoreProperties.add("id");
        page.getRecords().forEach(x -> {
            x.setCourseCover(COSUtil.getFileUrl(x.getCourseCover()));
            Map map = new HashMap<>();
            BeanUtil.beanToMap(x, map, true, key -> ignoreProperties.contains(key) ? null : key);
            map.put("id", x.getId().toString());
            map.put("teacherName", userClient.getOneTeacherInfo(x.getTeacherId()).getRealName());
            map.put("classList", classClient.getAllClassByCourseId(x.getId()));
            map.put("classPlastic", coursePlasticClient.getCoursePlasticByCourseId(x.getId()));
            map.put("courseComment", commentService.getCommentByCourseId(x.getId()));
            records.add(map);
        });
        mapPage.setRecords(records);
        return mapPage;
    }

    @Override
    public boolean setCourseNumber(Long id, Integer number) {
        LambdaUpdateWrapper<Course> wrapper = new LambdaUpdateWrapper<>();
        wrapper
                .eq(Course::getId, id)
                .set(Course::getNumber, number);
        return update(wrapper);
    }
}