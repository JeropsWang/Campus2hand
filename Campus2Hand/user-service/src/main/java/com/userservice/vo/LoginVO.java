package com.userservice.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginVO {
    @NotBlank(message="学号不能为空")
    private String studentId;

    @NotBlank(message="密码不能为空")
    private String password;
}
