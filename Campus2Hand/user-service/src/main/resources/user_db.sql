-- 创建数据库（如不存在）
CREATE DATABASE IF NOT EXISTS `user_db`
    DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `user_db`;
-- 用户表
CREATE TABLE `users` (
                         `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID，主键',
                         `student_id` VARCHAR(20) NOT NULL COMMENT '学号，唯一',
                         `password` VARCHAR(100) NOT NULL COMMENT '密码（加密存储）',
                         `name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
                         `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
                         `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
                         `college` VARCHAR(100) DEFAULT NULL COMMENT '学院',
                         `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
                         `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                         `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `uk_student_id` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 插入测试数据（符合接口示例：学号23111402）
INSERT INTO `users` (`student_id`, `password`, `name`, `nickname`, `phone`, `college`, `avatar`)
VALUES
    ('23111402', '123456', '王烨烽', '小烽', '13800138000', '软件学院', 'http://example.com/avatar1.jpg');