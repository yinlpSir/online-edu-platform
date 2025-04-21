package com.jh.education.common.mapper;

import com.jh.education.common.bean.LoginUser;
import com.jh.education.common.constant.Authentication;
import org.apache.ibatis.annotations.Select;

public interface CommonUserMapper {
    @Select("select id,username realUsername,role,gender,phone_number phoneNumber,password pwd,introduction,head_portrait headPortrait from user where phone_number=#{phoneNumber}")
    LoginUser selectOneByPhoneNumber(String phoneNumber);

    @Select("select count(*)!=0 from teacher where id=#{id} and is_authenticated=" + Authentication.PASSED)
    boolean teacherIsAuthentication(Long id);
}
