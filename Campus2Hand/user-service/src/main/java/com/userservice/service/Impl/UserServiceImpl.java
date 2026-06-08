package com.userservice.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.userservice.mapper.UserMapper;
import com.userservice.po.UserEntity;
import com.userservice.service.UserService;
import com.userservice.vo.LoginResponseVO;
import com.userservice.vo.LoginVO;
import com.userservice.vo.UpdateUserVO;
import com.userservice.vo.UserResponseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

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
}
