package com.userservice.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.userservice.mapper.UserMapper;
import com.userservice.po.UserEntity;
import com.userservice.service.UserService;
import com.userservice.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Value("${server.port:8081}")
    private String serverPort;

    @Value("${avatar.upload.path:./uploads/avatars}")
    private String uploadPath;

    public LoginResponseVO login(LoginVO loginVO) {
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserEntity::getStudentId, loginVO.getStudentId());
        UserEntity user=userMapper.selectOne(queryWrapper);
        if(user ==null){
            throw new RuntimeException("用户不存在");
        }
        if(!loginVO.getPassword().equals(user.getPassword())){
            throw new RuntimeException("密码错误");
        }

        LoginResponseVO responseVO = new LoginResponseVO();
        responseVO.setUserId(user.getId());
        responseVO.setNickname(user.getNickname());
        responseVO.setAvatar(user.getAvatar());
        responseVO.setName(user.getName());
        responseVO.setStudentId(user.getStudentId());


        return responseVO;
    }

    public UserResponseVO getUserInfo(Long userId) {
        UserResponseVO responseVO = new UserResponseVO();
         UserEntity user = userMapper.selectById(userId);
         if(user == null){
             throw new RuntimeException("用户不存在");
         }
         responseVO.setId(user.getId());
         responseVO.setNickname(user.getNickname());
         responseVO.setAvatar(user.getAvatar());
         responseVO.setName(user.getName());
         responseVO.setStudentId(user.getStudentId());

         return responseVO;
    }


    public UserResponseVO updateUserInfo(UpdateUserVO updateUserVO) {
        UpdateUserVO responseVO = new UpdateUserVO();
        UserEntity user = userMapper.selectById(updateUserVO.getId());
        if(user == null){
            throw new RuntimeException("用户不存在");
        }
        if(updateUserVO.getNickname() != null){
            user.setNickname(updateUserVO.getNickname());
        }
        if(updateUserVO.getPhone() != null){
            user.setPhone(updateUserVO.getPhone());
        }
        if(updateUserVO.getAvatar() != null){
            user.setAvatar(updateUserVO.getAvatar());
        }
        userMapper.updateById(user);

        return getUserInfo(updateUserVO.getId());

    }

    @Override
    public UploadResponseVO uploadAvatar(Long userId, MultipartFile file) {
        // 检查用户是否存在
        UserEntity user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 检查文件
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("请选择要上传的文件");
        }

        // 获取文件扩展名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".") 
            ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
            : ".png";

        // 生成唯一文件名
        String newFilename = UUID.randomUUID().toString() + extension;

        try {
            // 创建上传目录
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // 保存文件
            Path filePath = uploadDir.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath);

            // 构建访问URL
            String avatarUrl = "http://localhost:" + serverPort + "/avatars/" + newFilename;

            // 更新用户头像
            user.setAvatar(avatarUrl);
            userMapper.updateById(user);

            // 返回结果
            UploadResponseVO responseVO = new UploadResponseVO();
            responseVO.setUrl(avatarUrl);
            responseVO.setFilename(newFilename);

            return responseVO;

        } catch (IOException e) {
            throw new RuntimeException("上传失败: " + e.getMessage());
        }
    }

    @Override
    public void changePassword(ChangePasswordVO changePasswordVO) {
        // 检查用户是否存在
        UserEntity user = userMapper.selectById(changePasswordVO.getUserId());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证旧密码
        if (!changePasswordVO.getOldPassword().equals(user.getPassword())) {
            throw new RuntimeException("旧密码错误");
        }

        // 更新密码
        user.setPassword(changePasswordVO.getNewPassword());
        userMapper.updateById(user);
    }
}
