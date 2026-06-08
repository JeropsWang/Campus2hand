package com.userservice.dto;

import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String studentId;
    private String name;
    private String nickname;
    private String phone;
    private String college;
    private String avatar;
}
