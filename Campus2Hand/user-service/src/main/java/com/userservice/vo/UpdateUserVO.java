package com.userservice.vo;

import lombok.Data;

@Data
public class UpdateUserVO {

    private Long id;
    private String nickname;
    private String phone;
    private String avatar;
}
