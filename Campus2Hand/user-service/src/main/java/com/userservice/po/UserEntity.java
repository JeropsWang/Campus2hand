package com.userservice.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_db")
public class UserEntity {
    @TableId(type= IdType.AUTO)
    private long id;
    @TableField("student_id")
    private String studentId;

    private String password;
    private String name;
    private String nickname;
    private String phone;
    private String college;
    private String avatar;

    @TableField(fill= FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill=FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


}
