package com.userservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.userservice.po.UserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {

}
