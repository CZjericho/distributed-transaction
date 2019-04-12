/*
Navicat MySQL Data Transfer

Source Server         : ali_ECS_cmy
Source Server Version : 50723
Source Database       : order

Target Server Type    : MYSQL
Target Server Version : 50723
File Encoding         : 65001

Date: 2019-04-12 13:55:46
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `order`
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
  `id` tinyint(2) NOT NULL AUTO_INCREMENT,
  `order_no` varchar(20) NOT NULL DEFAULT '0' COMMENT '编号',
  `user_id` int(10) NOT NULL DEFAULT '0' COMMENT '会员id',
  `order_money` double(10,2) NOT NULL,
  `order_goods_name` varchar(20) DEFAULT '' COMMENT '订单名称',
  `order_date` datetime DEFAULT NULL,
  `version` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=83 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of order
-- ----------------------------
INSERT INTO `order` VALUES ('82', '8592', '1', '30.00', '??', '2019-04-11 10:04:42', '0');
