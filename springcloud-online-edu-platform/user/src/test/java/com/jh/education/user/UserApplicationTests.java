package com.jh.education.user;

import com.jh.education.common.constant.UserRole;
import com.jh.education.user.entity.User;
import com.jh.education.user.service.UserService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class UserApplicationTests {

    @Resource
    private UserService userService;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Test
    void test() {
//        User user = new User();
//        user.setUsername("admin");
//        user.setPassword(passwordEncoder.encode("admin"));
//        user.setGender(true);
//        user.setRole(UserRole.ADMIN);
//        user.setPhoneNumber("13873661395");
//        userService.save(user);
        System.out.println(passwordEncoder.encode("zs"));
    }

}
