package com.userservice.bo;

import lombok.Data;

@Data
public class UserBo {
    private Long id;
    private String studentId;
    private String password;
    private String name;
    private String nickname;
    private String phone;
    private String college;
    private String avatar;


}
