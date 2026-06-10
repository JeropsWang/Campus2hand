package com.userservice.service;

import com.userservice.vo.*;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    LoginResponseVO login(LoginVO loginVO);
    UserResponseVO getUserInfo(Long userId);
    UserResponseVO updateUserInfo(UpdateUserVO updateUserVO);
    UploadResponseVO uploadAvatar(Long userId, MultipartFile file);
    void changePassword(ChangePasswordVO changePasswordVO);
}
