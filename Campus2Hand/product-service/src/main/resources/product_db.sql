/*
 Navicat Premium Dump SQL

 Source Server         : JavaWeb
 Source Server Type    : MySQL
 Source Server Version : 80041 (8.0.41)
 Source Host           : localhost:3306
 Source Schema         : product_db

 Target Server Type    : MySQL
 Target Server Version : 80041 (8.0.41)
 File Encoding         : 65001

 Date: 13/06/2026 22:05:02
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `price` decimal(10, 2) NOT NULL,
  `quality` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `publish_time` datetime NULL DEFAULT NULL,
  `status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of product
-- ----------------------------
INSERT INTO `product` VALUES (1, '数码电子', '二手手机', '99新,功能无损坏', 3999.00, '99新', '/products/1.jpg', '2026-06-08 22:12:51', 'ACTIVE');
INSERT INTO `product` VALUES (2, '衣服', '衣服', '全新,功能无损坏', 3999.00, '全新', '', '2026-06-08 22:13:55', 'ACTIVE');
INSERT INTO `product` VALUES (3, '数码电子', '二手荣耀30pro手机', '89新,功能无损坏，可用正常的使用', 1000.00, '89新', '', '2026-06-08 22:37:36', 'ACTIVE');
INSERT INTO `product` VALUES (4, '数码电子', '这商品你买不起', '总之如此', 87.00, '95新', '', '2026-06-13 19:18:26', 'ACTIVE');
INSERT INTO `product` VALUES (5, '数码电子', '这是牢爱', '你想都别想要', 566.97, '7成新及以下', '', '2026-06-13 20:12:36', 'ACTIVE');
INSERT INTO `product` VALUES (6, '数码电子', '呼呼呼', '235额外tf', 325.00, '99新', '/products/6.jpg', '2026-06-13 20:20:57', 'ACTIVE');
INSERT INTO `product` VALUES (7, '数码电子', 'eg3q4t54', '23553asrfaw', 63455.00, '99新', '/products/7.jpeg', '2026-06-13 20:33:43', 'ACTIVE');
INSERT INTO `product` VALUES (8, '数码电子', '我理解了', 'fgswedfA', 322.00, '99新', '/products/8.jpg', '2026-06-13 21:04:46', 'ACTIVE');

SET FOREIGN_KEY_CHECKS = 1;
