-- 订单表测试数据
-- 用户ID: 1 (对应学号20240001)
-- 商品ID: 1-10 (对应商品表中的商品)

-- 1. 待支付订单
INSERT INTO orders (order_no, user_id, product_id, product_name, product_image, price, quantity, total_amount, status, trade_type, trade_location, trade_time, remark, create_time) 
VALUES ('ORD202406090001', 1, 1, '苹果笔记本电脑', 'https://example.com/macbook.jpg', 3500.00, 1, 3500.00, 'PENDING_PAYMENT', 'FACE_TO_FACE', '图书馆门口', NULL, '想要购买这台电脑', '2024-06-09 10:00:00');

-- 2. 待交付订单
INSERT INTO orders (order_no, user_id, product_id, product_name, product_image, price, quantity, total_amount, status, trade_type, trade_location, trade_time, remark, create_time) 
VALUES ('ORD202406090002', 1, 2, '机械键盘', 'https://example.com/keyboard.jpg', 180.00, 1, 180.00, 'PENDING_DELIVERY', 'SELF_PICKUP', '学生宿舍A栋', '2024-06-10 14:00:00', NULL, '2024-06-08 15:30:00');

-- 3. 已完成订单
INSERT INTO orders (order_no, user_id, product_id, product_name, product_image, price, quantity, total_amount, status, trade_type, trade_location, trade_time, remark, create_time) 
VALUES ('ORD202406090003', 1, 3, '考研数学真题', 'https://example.com/math.jpg', 35.00, 1, 35.00, 'COMPLETED', 'FACE_TO_FACE', '食堂门口', '2024-06-05 12:00:00', '成色很好', '2024-06-01 09:00:00');

-- 4. 已完成订单
INSERT INTO orders (order_no, user_id, product_id, product_name, product_image, price, quantity, total_amount, status, trade_type, trade_location, trade_time, remark, create_time) 
VALUES ('ORD202406090004', 1, 4, '蓝牙耳机', 'https://example.com/airpods.jpg', 89.00, 1, 89.00, 'COMPLETED', 'SELF_PICKUP', '教学楼B栋', '2024-06-03 18:00:00', '全新未拆封', '2024-06-02 20:15:00');

-- 5. 已关闭订单
INSERT INTO orders (order_no, user_id, product_id, product_name, product_image, price, quantity, total_amount, status, trade_type, trade_location, trade_time, remark, cancel_reason, cancel_time, create_time) 
VALUES ('ORD202406090005', 1, 5, '滑板', 'https://example.com/skateboard.jpg', 150.00, 1, 150.00, 'CLOSED', 'FACE_TO_FACE', '操场旁边', NULL, '尺寸不合适', '卖家取消了交易', '2024-06-07 16:00:00', '2024-06-06 11:00:00');

-- 6. 待支付订单
INSERT INTO orders (order_no, user_id, product_id, product_name, product_image, price, quantity, total_amount, status, trade_type, trade_location, trade_time, remark, create_time) 
VALUES ('ORD202406090006', 1, 6, '台灯', 'https://example.com/lamp.jpg', 45.00, 1, 45.00, 'PENDING_PAYMENT', 'SELF_PICKUP', '图书馆', NULL, NULL, '2024-06-09 08:30:00');

-- 7. 待交付订单
INSERT INTO orders (order_no, user_id, product_id, product_name, product_image, price, quantity, total_amount, status, trade_type, trade_location, trade_time, remark, create_time) 
VALUES ('ORD202406090007', 1, 7, '羽毛球拍', 'https://example.com/batminton.jpg', 99.00, 2, 198.00, 'PENDING_DELIVERY', 'FACE_TO_FACE', '体育馆', '2024-06-11 10:00:00', '买两副', '2024-06-08 14:45:00');

-- 8. 已完成订单
INSERT INTO orders (order_no, user_id, product_id, product_name, product_image, price, quantity, total_amount, status, trade_type, trade_location, trade_time, remark, create_time) 
VALUES ('ORD202406090008', 1, 8, '四级真题试卷', 'https://example.com/exam.jpg', 25.00, 1, 25.00, 'COMPLETED', 'SELF_PICKUP', '宿舍楼下', '2024-05-28 17:00:00', '还没做过', '2024-05-25 10:20:00');
