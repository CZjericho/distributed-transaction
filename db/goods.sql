/*
Navicat MySQL Data Transfer

Source Server         : ali_ECS_cmy
Source Server Version : 50723
Source Host           : 47.98.52.53:3306
Source Database       : goods

Target Server Type    : MYSQL
Target Server Version : 50723
File Encoding         : 65001

Date: 2019-04-12 14:23:53
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `goods`
-- ----------------------------
DROP TABLE IF EXISTS `goods`;
CREATE TABLE `goods` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `goods_name` varchar(20) NOT NULL DEFAULT '' COMMENT '商品名字',
  `goods_money` double(5,2) NOT NULL DEFAULT '0.00',
  `goods_count` int(10) NOT NULL DEFAULT '0' COMMENT '库存',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of goods
-- ----------------------------
INSERT INTO `goods` VALUES ('1', '??', '30.00', '0');
