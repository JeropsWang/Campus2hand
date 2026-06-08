package com.userservice.service;

import com.userservice.vo.LoginResponseVO;
import com.userservice.vo.LoginVO;
import com.userservice.vo.UpdateUserVO;
import com.userservice.vo.UserResponseVO;
import org.springframework.stereotype.Service;



public interface UserService {
    LoginResponseVO login(LoginVO loginVO);
    UserResponseVO getUserInfo(Long userId);
    UserResponseVO updateUserInfo(UpdateUserVO updateUserVO);
}
