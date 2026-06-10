/*
 Navicat Premium Dump SQL

 Source Server         : JavaWeb
 Source Server Type    : MySQL
 Source Server Version : 80041 (8.0.41)
 Source Host           : localhost:3306
 Source Schema         : product

 Target Server Type    : MySQL
 Target Server Version : 80041 (8.0.41)
 File Encoding         : 65001

 Date: 11/06/2026 00:44:20
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
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product
-- ----------------------------
INSERT INTO `product` VALUES (1, '数码电子', '二手手机', '99新,功能无损坏', 3999.00, '99新', '', '2026-06-08 22:12:51', 'ACTIVE');
INSERT INTO `product` VALUES (2, '2', '衣服', '全新,功能无损坏', 3999.00, '全新', '', '2026-06-08 22:13:55', 'ACTIVE');
INSERT INTO `product` VALUES (3, '1', '二手荣耀30pro手机', '89新,功能无损坏，可用正常的使用', 1000.00, '89新', '', '2026-06-08 22:37:36', 'ACTIVE');

SET FOREIGN_KEY_CHECKS = 1;
