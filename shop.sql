/*
SQLyog Professional v12.09 (64 bit)
MySQL - 5.7.26-0ubuntu0.16.04.1 : Database - shop
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`shop` /*!40100 DEFAULT CHARACTER SET latin1 */;

USE `shop`;

/*Table structure for table `activity` */

DROP TABLE IF EXISTS `activity`;

CREATE TABLE `activity` (
  `activityId` int(12) NOT NULL,
  `activityName` varchar(50) NOT NULL,
  `activityDes` varchar(500) NOT NULL,
  `discount` float DEFAULT '1',
  `fullPrice` int(12) DEFAULT NULL,
  `reducePrice` int(12) DEFAULT NULL,
  `fullNum` int(12) DEFAULT NULL,
  `reduceNum` int(12) DEFAULT NULL,
  PRIMARY KEY (`activityId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `address` */

DROP TABLE IF EXISTS `address`;

CREATE TABLE `address` (
  `addressID` int(12) NOT NULL,
  `userId` int(12) NOT NULL,
  `province` varchar(50) NOT NULL,
  `city` varchar(50) NOT NULL,
  `county` varchar(50) NOT NULL,
  `detailAddr` varchar(50) NOT NULL,
  `conName` varchar(50) NOT NULL,
  `conTel` varchar(50) NOT NULL,
  PRIMARY KEY (`addressID`),
  KEY `userId` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `admin` */

DROP TABLE IF EXISTS `admin`;

CREATE TABLE `admin` (
  `adminId` int(12) NOT NULL,
  `adminName` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  PRIMARY KEY (`adminId`),
  KEY `adminName` (`adminName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `admin` (`adminId`, `adminName`, `password`) VALUES
(1, 'admin', '123456');

/*Table structure for table `cache` */

DROP TABLE IF EXISTS `cache`;

CREATE TABLE `cache` (
  `c_id` int(11) NOT NULL AUTO_INCREMENT,
  `c_name` varchar(99) NOT NULL,
  `c_index` bigint(20) NOT NULL DEFAULT '0',
  `c_host` char(255) DEFAULT NULL,
  `c_host2` char(255) DEFAULT NULL,
  `c_stamp` bigint(20) DEFAULT NULL,
  `c_stamp2` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`c_id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=latin1;

/*Table structure for table `category` */

insert into `cache` (`c_id`, `c_name`, `c_index`, `c_host`, `c_host2`, `c_stamp`, `c_stamp2`) values('1','Activity','0',NULL,NULL,NULL,NULL);
insert into `cache` (`c_id`, `c_name`, `c_index`, `c_host`, `c_host2`, `c_stamp`, `c_stamp2`) values('2','Address','0','http://127.0.1.1:8081',NULL,'1563709565',NULL);
insert into `cache` (`c_id`, `c_name`, `c_index`, `c_host`, `c_host2`, `c_stamp`, `c_stamp2`) values('3','Admin','0','http://127.0.1.1:8080','http://127.0.1.1:8080','1561396854','1561396718');
insert into `cache` (`c_id`, `c_name`, `c_index`, `c_host`, `c_host2`, `c_stamp`, `c_stamp2`) values('4','Category','0',NULL,NULL,NULL,NULL);
insert into `cache` (`c_id`, `c_name`, `c_index`, `c_host`, `c_host2`, `c_stamp`, `c_stamp2`) values('5','Chat','0',NULL,NULL,NULL,NULL);
insert into `cache` (`c_id`, `c_name`, `c_index`, `c_host`, `c_host2`, `c_stamp`, `c_stamp2`) values('6','Comment','0',NULL,NULL,NULL,NULL);
insert into `cache` (`c_id`, `c_name`, `c_index`, `c_host`, `c_host2`, `c_stamp`, `c_stamp2`) values('7','Deliver','0',NULL,NULL,NULL,NULL);
insert into `cache` (`c_id`, `c_name`, `c_index`, `c_host`, `c_host2`, `c_stamp`, `c_stamp2`) values('8','Favorite','0',NULL,NULL,NULL,NULL);
insert into `cache` (`c_id`, `c_name`, `c_index`, `c_host`, `c_host2`, `c_stamp`, `c_stamp2`) values('9','Goods','0',NULL,NULL,NULL,NULL);
insert into `cache` (`c_id`, `c_name`, `c_index`, `c_host`, `c_host2`, `c_stamp`, `c_stamp2`) values('10','ImagePath','0',NULL,NULL,NULL,NULL);
insert into `cache` (`c_id`, `c_name`, `c_index`, `c_host`, `c_host2`, `c_stamp`, `c_stamp2`) values('11','Order','0',NULL,NULL,NULL,NULL);
insert into `cache` (`c_id`, `c_name`, `c_index`, `c_host`, `c_host2`, `c_stamp`, `c_stamp2`) values('12','ShopCart','0',NULL,NULL,NULL,NULL);
insert into `cache` (`c_id`, `c_name`, `c_index`, `c_host`, `c_host2`, `c_stamp`, `c_stamp2`) values('13','User','0',NULL,NULL,NULL,NULL);
insert into `cache` (`c_id`, `c_name`, `c_index`, `c_host`, `c_host2`, `c_stamp`, `c_stamp2`) values('14','Address_User','0',NULL,NULL,NULL,NULL);
insert into `cache` (`c_id`, `c_name`, `c_index`, `c_host`, `c_host2`, `c_stamp`, `c_stamp2`) values('15','Chat_User','0',NULL,NULL,NULL,NULL);
insert into `cache` (`c_id`, `c_name`, `c_index`, `c_host`, `c_host2`, `c_stamp`, `c_stamp2`) values('16','Comment_Goods','0',NULL,NULL,NULL,NULL);
insert into `cache` (`c_id`, `c_name`, `c_index`, `c_host`, `c_host2`, `c_stamp`, `c_stamp2`) values('17','Imagepath_Goods','0',NULL,NULL,NULL,NULL);
insert into `cache` (`c_id`, `c_name`, `c_index`, `c_host`, `c_host2`, `c_stamp`, `c_stamp2`) values('18','Orderitem_Order','0',NULL,NULL,NULL,NULL);
insert into `cache` (`c_id`, `c_name`, `c_index`, `c_host`, `c_host2`, `c_stamp`, `c_stamp2`) values('19','Order_User','0',NULL,NULL,NULL,NULL);
insert into `cache` (`c_id`, `c_name`, `c_index`, `c_host`, `c_host2`, `c_stamp`, `c_stamp2`) values('20','Shopcart_User','0',NULL,NULL,NULL,NULL);

DROP TABLE IF EXISTS `category`;

CREATE TABLE `category` (
  `cateId` int(12) NOT NULL,
  `cateName` varchar(50) NOT NULL,
  PRIMARY KEY (`cateId`),
  UNIQUE KEY `cateName` (`cateName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `category` (`cateId`, `cateName`) VALUES
(1, 'Digital'),
(2, 'Clothes'),
(3, 'Appliances'),
(4, 'Book');

/*Table structure for table `chat` */

DROP TABLE IF EXISTS `chat`;

CREATE TABLE `chat` (
  `chatId` int(20) NOT NULL,
  `sendUser` int(12) NOT NULL,
  `receiveUser` int(12) NOT NULL,
  `MsgContent` varchar(255) NOT NULL,
  `MsgTime` datetime NOT NULL,
  PRIMARY KEY (`chatId`),
  KEY `sendUser` (`sendUser`),
  KEY `receiveUser` (`receiveUser`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `comment` */

DROP TABLE IF EXISTS `comment`;

CREATE TABLE `comment` (
  `commentId` int(12) NOT NULL,
  `userId` int(12) NOT NULL,
  `goodsId` int(12) NOT NULL,
  `point` int(8) NOT NULL,
  `content` varchar(255) NOT NULL,
  `commentTime` datetime NOT NULL,
  PRIMARY KEY (`commentId`),
  KEY `userId` (`userId`),
  KEY `goodsId` (`goodsId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `favorite` */

DROP TABLE IF EXISTS `favorite`;

CREATE TABLE `favorite` (
  `favoriteId` int(12) NOT NULL,
  `userId` int(12) NOT NULL,
  `goodsId` int(12) NOT NULL,
  `collectTime` datetime NOT NULL,
  PRIMARY KEY (`favoriteId`),
  KEY `collection_ibfk_2` (`goodsId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `goods` */

DROP TABLE IF EXISTS `goods`;

CREATE TABLE `goods` (
  `goodsId` int(12) NOT NULL,
  `goodsName` varchar(50) NOT NULL,
  `price` int(12) NOT NULL,
  `num` int(12) NOT NULL,
  `upTime` datetime NOT NULL,
  `category` int(12) NOT NULL,
  `detailCate` varchar(50) NOT NULL,
  `description` text NOT NULL,
  `activityId` int(12) NOT NULL DEFAULT '1',
  PRIMARY KEY (`goodsId`),
  KEY `activityId` (`activityId`),
  KEY `category` (`category`),
  KEY `goodsName` (`goodsName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `imagepath` */

DROP TABLE IF EXISTS `imagepath`;

CREATE TABLE `imagepath` (
  `pathId` int(12) NOT NULL,
  `goodId` int(12) NOT NULL,
  `path` varchar(255) NOT NULL,
  PRIMARY KEY (`pathId`),
  KEY `goodid` (`goodId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `order` */

DROP TABLE IF EXISTS `order`;

CREATE TABLE `order` (
  `orderId` int(12) NOT NULL,
  `userId` int(12) NOT NULL,
  `orderTime` datetime NOT NULL,
  `oldPrice` float NOT NULL,
  `newPrice` float NOT NULL,
  `isPay` tinyint(1) NOT NULL,
  `isSend` tinyint(1) NOT NULL,
  `isReceive` tinyint(1) NOT NULL,
  `isComplete` tinyint(1) NOT NULL,
  `addressId` int(12) NOT NULL,
  PRIMARY KEY (`orderId`),
  KEY `userId` (`userId`),
  KEY `addressId` (`addressId`),
  KEY `orderTime` (`orderTime`),
  KEY `isPay` (`isPay`),
  KEY `isSend` (`isSend`),
  KEY `isReceive` (`isReceive`),
  KEY `isComplete` (`isComplete`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `orderitem` */

DROP TABLE IF EXISTS `orderitem`;

CREATE TABLE `orderitem` (
  `itemId` int(12) NOT NULL,
  `orderId` int(12) NOT NULL,
  `goodsId` int(12) NOT NULL,
  `num` int(12) NOT NULL,
  PRIMARY KEY (`itemId`),
  KEY `orderId` (`orderId`),
  KEY `goodsId` (`goodsId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `shopcart` */

DROP TABLE IF EXISTS `shopcart`;

CREATE TABLE `shopcart` (
  `shopcartId` int(12) NOT NULL,
  `userId` int(12) NOT NULL,
  `goodsid` int(12) NOT NULL,
  `cateDate` datetime NOT NULL,
  `goodsNum` int(12) NOT NULL,
  PRIMARY KEY (`shopcartId`),
  KEY `userId` (`userId`),
  KEY `goodsid` (`goodsid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `userId` int(12) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `regTime` datetime NOT NULL,
  `email` varchar(50) NOT NULL,
  `telephone` varchar(50) NOT NULL,
  PRIMARY KEY (`userId`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
