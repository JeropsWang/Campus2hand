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
        // 确保返回相对路径格式
        responseVO.setAvatar(normalizeAvatarUrl(user.getAvatar()));
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
         // 确保返回相对路径格式
         responseVO.setAvatar(normalizeAvatarUrl(user.getAvatar()));
         responseVO.setName(user.getName());
         responseVO.setStudentId(user.getStudentId());
         responseVO.setPhone(user.getPhone());
         responseVO.setCollege(user.getCollege());

         return responseVO;
    }

    /**
     * 规范化头像URL，确保返回相对路径格式
     * @param avatarUrl 原始头像URL
     * @return 规范化后的相对路径
     */
    private String normalizeAvatarUrl(String avatarUrl) {
        if (avatarUrl == null || avatarUrl.isEmpty()) {
            return null;
        }

        // 如果是绝对URL，提取相对路径部分
        if (avatarUrl.startsWith("http://") || avatarUrl.startsWith("https://")) {
            try {
                // 提取 /avatars/xxx 部分
                if (avatarUrl.contains("/avatars/")) {
                    return avatarUrl.substring(avatarUrl.indexOf("/avatars/"));
                }
            } catch (Exception e) {
                // 解析失败，返回原值
                return avatarUrl;
            }
        }

        // 如果已经是相对路径，直接返回
        if (avatarUrl.startsWith("/avatars/")) {
            return avatarUrl;
        }

        // 如果是纯文件名，添加路径前缀
        if (!avatarUrl.startsWith("/")) {
            return "/avatars/" + avatarUrl;
        }

        return avatarUrl;
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

        // 使用用户ID命名头像文件（格式：用户ID.扩展名）
        String newFilename = userId + extension;

        try {
            // 创建上传目录
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // 删除旧头像文件（如果存在）
            deleteOldAvatar(user.getAvatar(), uploadDir);

            // 保存新文件
            Path filePath = uploadDir.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath);

            // 构建访问URL（使用相对路径，前端通过代理访问）
            String avatarUrl = "/avatars/" + newFilename;

            // 更新用户头像（存储相对路径）
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

    /**
     * 删除旧头像文件
     * @param oldAvatarUrl 旧头像URL（可能是完整URL或相对路径）
     * @param uploadDir 上传目录路径
     */
    private void deleteOldAvatar(String oldAvatarUrl, Path uploadDir) {
        if (oldAvatarUrl == null || oldAvatarUrl.isEmpty()) {
            return;
        }

        try {
            // 提取文件名（支持完整URL和相对路径）
            String filename;
            if (oldAvatarUrl.contains("/")) {
                filename = oldAvatarUrl.substring(oldAvatarUrl.lastIndexOf("/") + 1);
            } else {
                filename = oldAvatarUrl;
            }

            // 构建旧文件路径
            Path oldFilePath = uploadDir.resolve(filename);
            
            // 如果文件存在，删除它
            if (Files.exists(oldFilePath)) {
                Files.delete(oldFilePath);
                System.out.println("Deleted old avatar: " + filename);
            }
        } catch (IOException e) {
            System.err.println("Failed to delete old avatar: " + e.getMessage());
            // 删除失败不影响新头像上传
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
