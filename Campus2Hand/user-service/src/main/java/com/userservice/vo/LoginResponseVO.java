package com.userservice.vo;

import lombok.Data;

@Data
public class LoginResponseVO {
    private Long UserId;
    private String studentId;
    private String name;
    private String nickname;
    private String avatar;

}
