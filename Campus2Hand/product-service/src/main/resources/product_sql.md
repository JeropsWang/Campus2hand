-- 创建数据库
CREATE DATABASE IF NOT EXISTS product_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE product_db;

-- 创建商品表 product
CREATE TABLE IF NOT EXISTS product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '商品ID（主键自增）',
    category VARCHAR(100) NOT NULL COMMENT '商品分类',
    title VARCHAR(200) NOT NULL COMMENT '商品标题',
    description TEXT COMMENT '商品描述',
    price DECIMAL(10,2) NOT NULL COMMENT '商品价格',
    quality VARCHAR(50) COMMENT '商品成色（如：全新/95新/二手）',
    image_url VARCHAR(500) COMMENT '商品图片URL',
    publish_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    status ENUM('上架','下架','审核中') DEFAULT '审核中' COMMENT '商品状态'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品服务数据表';