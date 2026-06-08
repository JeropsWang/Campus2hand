package com.userservice.controller;

import com.userservice.service.UserService;
import com.userservice.utils.Result;
import com.userservice.vo.LoginResponseVO;
import com.userservice.vo.LoginVO;
import com.userservice.vo.UpdateUserVO;
import com.userservice.vo.UserResponseVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;
    /*
    * 用户登录
    * */
    @PostMapping("/login")
    public Result<LoginResponseVO> login(@Valid @RequestBody LoginVO loginVO) {
        // 调用服务层进行登录逻辑处理
        LoginResponseVO response = userService.login(loginVO);
        return Result.success(response);

    }

    @GetMapping("/{userId}")
    public Result<UserResponseVO> getUserInfo(@PathVariable Long userId) {
        UserResponseVO response = userService.getUserInfo(userId);
        return Result.success(response);
    }

    @PutMapping("/Info")
    public Result<UserResponseVO> updateUserInfo(@Valid @RequestBody UpdateUserVO updateUserVO) {
        UserResponseVO response = userService.updateUserInfo(updateUserVO);
        return Result.success(response);
    }
}
