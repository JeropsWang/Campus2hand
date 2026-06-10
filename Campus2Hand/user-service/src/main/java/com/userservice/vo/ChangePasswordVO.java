package com.userservice.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePasswordVO {
    @NotBlank(message = "用户ID不能为空")
    private Long userId;
    
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;
    
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}