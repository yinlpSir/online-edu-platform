package com.jh.education.user.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author jh
 * @since 2023-07-14
 */
@Getter
@Setter
public class User {
    @TableId
    private Long id;

    private String username;

    private String password;

    private String phoneNumber;

    private Boolean gender;

    private String headPortrait;

    private String introduction;

    private Byte role;

    @TableLogic
    private Boolean isDeleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
