package com.userservice.vo;

import lombok.Data;

@Data
public class UserResponseVO {
    private Long id;
    private String studentId;
    private String name;
    private String nickname;
    private String phone;
    private String college;
    private String avatar;
}
