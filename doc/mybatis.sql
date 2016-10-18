-- MySQL dump 10.13  Distrib 5.6.15, for Win64 (x86_64)
--
-- Host: localhost    Database: mybatis
-- ------------------------------------------------------
-- Server version	5.6.15

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `mybatis`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `mybatis` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `mybatis`;

--
-- Table structure for table `t_lecture`
--

DROP TABLE IF EXISTS `t_lecture`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_lecture` (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `lecture_name` varchar(60) NOT NULL COMMENT '课程名称',
  `note` varchar(1024) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_lecture`
--

LOCK TABLES `t_lecture` WRITE;
/*!40000 ALTER TABLE `t_lecture` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_lecture` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_role`
--

DROP TABLE IF EXISTS `t_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_role` (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `role_name` varchar(60) NOT NULL COMMENT '角色名称',
  `note` varchar(1024) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_role`
--

LOCK TABLES `t_role` WRITE;
/*!40000 ALTER TABLE `t_role` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_student`
--

DROP TABLE IF EXISTS `t_student`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_student` (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `cnname` varchar(60) NOT NULL COMMENT '学生姓名',
  `sex` tinyint(4) NOT NULL COMMENT '性别',
  `selfcard_no` int(20) NOT NULL COMMENT '学生证号',
  `note` varchar(1024) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='学生信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_student`
--

LOCK TABLES `t_student` WRITE;
/*!40000 ALTER TABLE `t_student` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_student` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_student_health_female`
--

DROP TABLE IF EXISTS `t_student_health_female`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_student_health_female` (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `student_id` int(20) DEFAULT NULL COMMENT '学生编号',
  `check_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '检查时间',
  `heart` varchar(60) NOT NULL COMMENT '心',
  `liver` varchar(60) NOT NULL COMMENT '肝',
  `spleen` varchar(60) NOT NULL COMMENT '脾',
  `lung` varchar(60) NOT NULL COMMENT '肺',
  `kidney` varchar(60) NOT NULL COMMENT '肾',
  `uterus` varchar(60) NOT NULL COMMENT '子宫',
  `note` varchar(1024) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `student_id` (`student_id`),
  CONSTRAINT `t_student_health_female_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `t_student` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='女生学生健康信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_student_health_female`
--

LOCK TABLES `t_student_health_female` WRITE;
/*!40000 ALTER TABLE `t_student_health_female` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_student_health_female` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_student_health_male`
--

DROP TABLE IF EXISTS `t_student_health_male`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_student_health_male` (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `student_id` int(20) DEFAULT NULL COMMENT '学生编号',
  `check_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '检查时间',
  `heart` varchar(60) NOT NULL COMMENT '心',
  `liver` varchar(60) NOT NULL COMMENT '肝',
  `spleen` varchar(60) NOT NULL COMMENT '脾',
  `lung` varchar(60) NOT NULL COMMENT '肺',
  `kidney` varchar(60) NOT NULL COMMENT '肾',
  `prostate` varchar(60) NOT NULL COMMENT '前列腺',
  `note` varchar(1024) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `student_id` (`student_id`),
  CONSTRAINT `t_student_health_male_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `t_student` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='男生学生健康信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_student_health_male`
--

LOCK TABLES `t_student_health_male` WRITE;
/*!40000 ALTER TABLE `t_student_health_male` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_student_health_male` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_student_lecture`
--

DROP TABLE IF EXISTS `t_student_lecture`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_student_lecture` (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `student_id` int(20) DEFAULT NULL COMMENT '学生编号',
  `lecture_id` int(20) DEFAULT NULL COMMENT '课程编号',
  `grade` decimal(5,2) NOT NULL COMMENT '评分',
  `note` varchar(1024) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `student_id` (`student_id`),
  KEY `lecture_id` (`lecture_id`),
  CONSTRAINT `t_student_lecture_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `t_student` (`id`),
  CONSTRAINT `t_student_lecture_ibfk_2` FOREIGN KEY (`lecture_id`) REFERENCES `t_lecture` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='学生成绩表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_student_lecture`
--

LOCK TABLES `t_student_lecture` WRITE;
/*!40000 ALTER TABLE `t_student_lecture` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_student_lecture` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_student_selfcard`
--

DROP TABLE IF EXISTS `t_student_selfcard`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_student_selfcard` (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `student_id` int(20) DEFAULT NULL COMMENT '学生编号',
  `native` varchar(60) NOT NULL COMMENT '籍贯',
  `issue_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '发证日期',
  `end_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '结束日期',
  `note` varchar(1024) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `student_id` (`student_id`),
  CONSTRAINT `t_student_selfcard_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `t_student` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='身份证信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_student_selfcard`
--

LOCK TABLES `t_student_selfcard` WRITE;
/*!40000 ALTER TABLE `t_student_selfcard` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_student_selfcard` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user`
--

DROP TABLE IF EXISTS `t_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_name` varchar(60) NOT NULL COMMENT '用户名称',
  `cnname` varchar(60) NOT NULL COMMENT '姓名',
  `sex` tinyint(3) NOT NULL COMMENT '性别',
  `mobile` varchar(20) NOT NULL COMMENT '手机号码',
  `email` varchar(60) NOT NULL COMMENT '电子邮件',
  `note` varchar(1024) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user`
--

LOCK TABLES `t_user` WRITE;
/*!40000 ALTER TABLE `t_user` DISABLE KEYS */;
INSERT INTO `t_user` VALUES (1,'fasdfasdf','admin',0,'123123123123','fasfdasdf',NULL),(2,'fasdfasdf','admin',0,'123123123123','fasfdasdf',NULL),(3,'fasdfasdf','admin',0,'123123123123','fasfdasdf',NULL),(4,'fasdfasdf','admin',0,'123123123123','fasfdasdf',NULL),(5,'lisi','李四',0,'3123123123','123123123123afsd','火狐');
/*!40000 ALTER TABLE `t_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_role`
--

DROP TABLE IF EXISTS `t_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_user_role` (
  `user_id` bigint(20) NOT NULL COMMENT '用户编号',
  `role_id` int(20) NOT NULL COMMENT '角色编号',
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `role_id` (`role_id`),
  CONSTRAINT `t_user_role_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `t_user_role_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `t_role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_role`
--

LOCK TABLES `t_user_role` WRITE;
/*!40000 ALTER TABLE `t_user_role` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_role` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-10-18 17:26:17
