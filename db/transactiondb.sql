/*
Navicat MySQL Data Transfer

Source Server         : ali_ECS_cmy
Source Server Version : 50723
Source Database       : transactiondb

Target Server Type    : MYSQL
Target Server Version : 50723
File Encoding         : 65001

Date: 2019-04-12 13:55:02
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `transaction_log`
-- ----------------------------
DROP TABLE IF EXISTS `transaction_log`;
CREATE TABLE `transaction_log` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `centre_no` varchar(20) NOT NULL COMMENT '中心id',
  `count` tinyint(2) NOT NULL DEFAULT '2' COMMENT '操作条数',
  `prepare_count` tinyint(1) NOT NULL DEFAULT '2' COMMENT '准备个数',
  `failed_count` tinyint(1) NOT NULL DEFAULT '0' COMMENT '失败个数',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=206 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of transaction_log
-- ----------------------------
