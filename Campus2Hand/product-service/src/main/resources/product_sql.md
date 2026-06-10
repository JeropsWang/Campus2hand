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




-- 商品表 product 11条测试数据
INSERT INTO product (category, title, description, price, quality, image_url, publish_time, status)
VALUES
-- 1. 苹果笔记本电脑
('数码电子', '苹果笔记本电脑', '日常办公、学习使用，运行流畅，机身无明显磕碰', 3500.00, '95新', 'https://example.com/macbook.jpg', '2024-05-20 09:10:00', '上架'),
-- 2. 机械键盘
('数码配件', '机械键盘', '青轴机械键盘，按键清脆，灯光效果炫酷，使用半年', 180.00, '95新', 'https://example.com/keyboard.jpg', '2024-05-22 14:20:00', '上架'),
-- 3. 考研数学真题
('书籍资料', '考研数学真题', '历年考研数学真题合集，答案解析完整，字迹工整', 35.00, '二手', 'https://example.com/math.jpg', '2024-05-25 10:00:00', '上架'),
-- 4. 蓝牙耳机
('数码电子', '蓝牙耳机', '无线入耳式蓝牙耳机，续航持久，音质清晰', 89.00, '全新', 'https://example.com/airpods.jpg', '2024-05-26 16:30:00', '上架'),
-- 5. 滑板
('运动户外', '滑板', '双翘专业滑板，板面完好，轮子顺滑', 150.00, '二手', 'https://example.com/skateboard.jpg', '2024-05-28 11:15:00', '下架'),
-- 6. 台灯
('生活用品', '台灯', '护眼LED台灯，三档亮度调节，宿舍学习专用', 45.00, '95新', 'https://example.com/lamp.jpg', '2024-05-30 08:40:00', '上架'),
-- 7. 羽毛球拍
('运动户外', '羽毛球拍', '碳素羽毛球拍，重量轻盈，击球手感舒适', 99.00, '二手', 'https://example.com/batminton.jpg', '2024-06-01 15:00:00', '上架'),
-- 8. 四级真题试卷
('书籍资料', '四级真题试卷', '大学英语四级历年真题，附带听力光盘', 25.00, '二手', 'https://example.com/exam.jpg', '2024-06-02 09:25:00', '上架'),
-- 9. 新增商品1：电竞鼠标
('数码配件', '电竞游戏鼠标', '可编程电竞鼠标，有线连接，定位精准', 69.00, '全新', 'https://example.com/mouse.jpg', '2024-06-03 17:10:00', '审核中'),
-- 10. 新增商品2：保温杯
('生活用品', '大容量保温杯', '304不锈钢保温杯，保温时长可达12小时', 59.00, '全新', 'https://example.com/cup.jpg', '2024-06-04 10:30:00', '上架'),
-- 11. 新增商品3：篮球
('运动户外', '标准七号篮球', '室内外通用耐磨篮球，气密性良好', 129.00, '95新', 'https://example.com/basketball.jpg', '2024-06-05 14:50:00', '审核中');