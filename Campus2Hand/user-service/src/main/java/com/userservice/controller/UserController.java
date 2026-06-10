package com.userservice.controller;

import com.userservice.service.UserService;
import com.userservice.utils.Result;
import com.userservice.vo.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginResponseVO> login(@Valid @RequestBody LoginVO loginVO) {
        LoginResponseVO response = userService.login(loginVO);
        return Result.success(response);
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/{userId}")
    public Result<UserResponseVO> getUserInfo(@PathVariable Long userId) {
        UserResponseVO response = userService.getUserInfo(userId);
        return Result.success(response);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/info")
    public Result<UserResponseVO> updateUserInfo(@Valid @RequestBody UpdateUserVO updateUserVO) {
        UserResponseVO response = userService.updateUserInfo(updateUserVO);
        return Result.success(response);
    }

    /**
     * 上传头像
     */
    @PostMapping("/{userId}/avatar")
    public Result<UploadResponseVO> uploadAvatar(@PathVariable Long userId, @RequestParam("file") MultipartFile file) {
        UploadResponseVO response = userService.uploadAvatar(userId, file);
        return Result.success(response);
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordVO changePasswordVO) {
        userService.changePassword(changePasswordVO);
        return Result.success(null);
    }
}
