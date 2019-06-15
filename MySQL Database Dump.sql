CREATE DATABASE  IF NOT EXISTS `csc8005_team02` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `csc8005_team02`;
-- MySQL dump 10.13  Distrib 8.0.16, for Win64 (x86_64)
--
-- Host: localhost    Database: csc8005_team02
-- ------------------------------------------------------
-- Server version	5.6.19

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
 SET NAMES utf8 ;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `Accounts`
--

DROP TABLE IF EXISTS `Accounts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `Accounts` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `restaurant` tinyint(1) NOT NULL DEFAULT '0',
  `warehouse` tinyint(1) NOT NULL DEFAULT '0',
  `driver` tinyint(1) NOT NULL DEFAULT '0',
  `manager` tinyint(1) NOT NULL DEFAULT '0',
  `workLocation` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Accounts`
--

LOCK TABLES `Accounts` WRITE;
/*!40000 ALTER TABLE `Accounts` DISABLE KEYS */;
INSERT INTO `Accounts` VALUES (1,'warehouse','warehouse','ae1fd358c7612a02fdc6d923fd40308ebefb0e954c7ddb6f9a8bcdd1f3b00c3b',0,1,0,0,'2 Drum Rd, Chester le Street, DH2 1AB'),(2,'alnwick','alnwick','514c9fb6e19f9d8829084a01ee1315e41376e92a1321bb2f7c2d14d225ad5c32',1,0,0,0,'Alnwick Town Centre, 19 Lagny Street, Alnwick, NE66 1LA'),(3,'newton','newton','fd216818cecbc78c0aeb274521b1501a01a2226a23a9a6922abb824b12dd86c4',1,0,0,0,'Newton Aycliffe, 4 Northfield Way, Newton Aycliffe, DL5 6EJ'),(4,'seaton','seaton','0387cc3b356c2dd23588da696fb645b83dbdcf9635f923ab8d02c51a1e89b79d',1,0,0,0,'Seaton Burn Services, Fisher Lane, Newcastle upon Tyne NE13 6BP'),(5,'thirsk','thirsk','132890abfd36a3e796c7fcff6375453d9edad3644ffbd57baf828ed0c0677c82',1,0,0,0,'Thirsk Town Centre, 26 Market Place, Thirsk YO7 1LB'),(6,'whitby','whitby','f743f9cd149d600190ad281de8ffde9dff4f2afddfcba6f3cee21ede90c0fefe',1,0,0,0,'Whitby Town Centre, Station Square, Whitby YO21 1DX'),(7,'wareManager','wareManager','1fefe32651d9ad5bbc473889b4127656da69c21e189273c489f4bbbf334bd9dd',0,1,0,1,'2 Drum Rd, Chester le Street, DH2 1AB'),(8,'restManager','restManager','0b5e2603533f4658d7aed3ae1851a79387f19e64c931e7a24da2eef7288c375c',1,0,0,1,'Seaton Burn Services, Fisher Lane, Newcastle upon Tyne NE13 6BP'),(9,'driver','driver','b4def8217cadae26d4da633fd2a4e58e326cbb5d570afdc3989484da07af3579',0,0,1,0,'2 Drum Rd, Chester le Street, DH2 1AB');
/*!40000 ALTER TABLE `Accounts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Contains`
--

DROP TABLE IF EXISTS `Contains`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `Contains` (
  `orderId` int(11) NOT NULL,
  `quantity` int(11) NOT NULL,
  `stockItem` varchar(100) NOT NULL,
  PRIMARY KEY (`orderId`,`stockItem`),
  KEY `stockItem` (`stockItem`),
  CONSTRAINT `Contains_ibfk_1` FOREIGN KEY (`stockItem`) REFERENCES `Stock` (`stockItem`),
  CONSTRAINT `Contains_ibfk_2` FOREIGN KEY (`orderId`) REFERENCES `StockOrders` (`orderId`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Contains`
--

LOCK TABLES `Contains` WRITE;
/*!40000 ALTER TABLE `Contains` DISABLE KEYS */;
INSERT INTO `Contains` VALUES (1,10,'Cheese Slices'),(1,10,'Chicken Breast Fillets'),(1,10,'Mayonnaise'),(1,10,'Mycoprotein based meat substitute Southern fried burger'),(1,10,'Mycoprotein based meat substitute Southern fried Strips'),(1,10,'Shredded iceberg lettuce'),(2,200,'Cola syrup'),(3,30,'Hash Browns'),(4,40,'Shredded iceberg lettuce'),(5,50,'Sesame Seed Buns'),(6,100,'Chicken Pieces'),(6,80,'Uncooked French Fries'),(7,2,'Chicken Breast Fillets'),(7,99,'Mycoprotein based meat substitute Southern fried burger'),(7,89,'Mycoprotein based meat substitute Southern fried Strips'),(7,55,'Uncooked French Fries'),(8,1000,'Mayonnaise');
/*!40000 ALTER TABLE `Contains` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Inside`
--

DROP TABLE IF EXISTS `Inside`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `Inside` (
  `warehouseAddress` varchar(100) NOT NULL,
  `stockItem` varchar(100) NOT NULL,
  `quantity` int(11) NOT NULL,
  `minQuantity` int(11) NOT NULL,
  PRIMARY KEY (`warehouseAddress`,`stockItem`),
  KEY `stockItem` (`stockItem`),
  CONSTRAINT `Inside_ibfk_1` FOREIGN KEY (`warehouseAddress`) REFERENCES `Warehouse` (`warehouseAddress`),
  CONSTRAINT `Inside_ibfk_2` FOREIGN KEY (`stockItem`) REFERENCES `Stock` (`stockItem`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Inside`
--

LOCK TABLES `Inside` WRITE;
/*!40000 ALTER TABLE `Inside` DISABLE KEYS */;
INSERT INTO `Inside` VALUES ('2 Drum Rd, Chester le Street, DH2 1AB','Cheese Slices',0,100),('2 Drum Rd, Chester le Street, DH2 1AB','Chicken Breast Fillets',0,100),('2 Drum Rd, Chester le Street, DH2 1AB','Chicken Pieces',0,100),('2 Drum Rd, Chester le Street, DH2 1AB','Chicken strips',0,100),('2 Drum Rd, Chester le Street, DH2 1AB','Cola syrup',0,100),('2 Drum Rd, Chester le Street, DH2 1AB','Hash Browns',0,100),('2 Drum Rd, Chester le Street, DH2 1AB','Mayonnaise',0,100),('2 Drum Rd, Chester le Street, DH2 1AB','Mycoprotein based meat substitute Southern fried burger',0,100),('2 Drum Rd, Chester le Street, DH2 1AB','Mycoprotein based meat substitute Southern fried Strips',0,100),('2 Drum Rd, Chester le Street, DH2 1AB','Sesame Seed Buns',0,100),('2 Drum Rd, Chester le Street, DH2 1AB','Shredded iceberg lettuce',0,100),('2 Drum Rd, Chester le Street, DH2 1AB','Uncooked French Fries',0,100);
/*!40000 ALTER TABLE `Inside` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MadeWith`
--

DROP TABLE IF EXISTS `MadeWith`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `MadeWith` (
  `mealId` varchar(100) NOT NULL,
  `stockItem` varchar(100) NOT NULL,
  `quantity` int(11) NOT NULL,
  PRIMARY KEY (`mealId`,`stockItem`),
  KEY `stockItem` (`stockItem`),
  CONSTRAINT `MadeWith_ibfk_1` FOREIGN KEY (`mealId`) REFERENCES `Meals` (`mealId`),
  CONSTRAINT `MadeWith_ibfk_2` FOREIGN KEY (`stockItem`) REFERENCES `Stock` (`stockItem`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MadeWith`
--

LOCK TABLES `MadeWith` WRITE;
/*!40000 ALTER TABLE `MadeWith` DISABLE KEYS */;
INSERT INTO `MadeWith` VALUES ('3 Boneless Southern Fried Chicken Strips','Chicken Strips',3),('3 Pieces of Southern Fried Chicken','Chicken Pieces',3),('3 Vegetarian Southern Fried Strips','Mycoprotein based meat substitute Southern fried Strips',3),('CLUK Burger','Chicken Breast Fillets',1),('CLUK Burger','Mayonnaise',10),('CLUK Burger','Sesame seed buns',1),('CLUK Burger','Shredded iceberg lettuce',5),('CLUK Super Burger','Cheese Slices ',1),('CLUK Super Burger','Chicken Breast Fillets',1),('CLUK Super Burger','Hash Browns ',1),('CLUK Super Burger','Mayonnaise',10),('CLUK Super Burger','Sesame seed buns',1),('CLUK Super Burger','Shredded iceberg lettuce',5),('CLUK Vegetarian Burger','Mayonnaise',10),('CLUK Vegetarian Burger','Mycoprotein based meat substitute Southern fried burger',1),('CLUK Vegetarian Burger','Sesame seed buns',1),('CLUK Vegetarian Burger','Shredded iceberg lettuce',5),('CLUK Vegetarian Super Burger','Cheese Slices ',1),('CLUK Vegetarian Super Burger','Hash Browns ',1),('CLUK Vegetarian Super Burger','Mayonnaise',10),('CLUK Vegetarian Super Burger','Mycoprotein based meat substitute Southern fried burger',1),('CLUK Vegetarian Super Burger','Sesame seed buns',1),('CLUK Vegetarian Super Burger','Shredded iceberg lettuce',5),('Cola','Cola syrup ',50),('Fries','Uncooked French Fries',120);
/*!40000 ALTER TABLE `MadeWith` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Meals`
--

DROP TABLE IF EXISTS `Meals`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `Meals` (
  `mealId` varchar(200) NOT NULL,
  `price` decimal(3,2) NOT NULL,
  PRIMARY KEY (`mealId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Meals`
--

LOCK TABLES `Meals` WRITE;
/*!40000 ALTER TABLE `Meals` DISABLE KEYS */;
INSERT INTO `Meals` VALUES ('3 Boneless Southern Fried Chicken Strips',2.50),('3 Pieces of Southern Fried Chicken',2.50),('3 Vegetarian Southern Fried Strips',2.50),('CLUK Burger ',3.50),('CLUK Super Burger',4.50),('CLUK Vegetarian Burger',3.50),('CLUK Vegetarian Super Burger',4.50),('Cola ',0.99),('Fries ',0.99);
/*!40000 ALTER TABLE `Meals` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Orders`
--

DROP TABLE IF EXISTS `Orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `Orders` (
  `restaurantAddress` varchar(100) NOT NULL,
  `orderId` int(20) NOT NULL,
  PRIMARY KEY (`restaurantAddress`,`orderId`),
  KEY `Orders_ibfk_2` (`orderId`),
  CONSTRAINT `Orders_ibfk_2` FOREIGN KEY (`orderId`) REFERENCES `StockOrders` (`orderId`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `Orders_ibfk_1` FOREIGN KEY (`restaurantAddress`) REFERENCES `Restaurants` (`restaurantAddress`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Orders`
--

LOCK TABLES `Orders` WRITE;
/*!40000 ALTER TABLE `Orders` DISABLE KEYS */;
INSERT INTO `Orders` VALUES ('Alnwick Town Centre, 19 Lagny Street, Alnwick, NE66 1LA',1),('Newton Aycliffe, 4 Northfield Way, Newton Aycliffe, DL5 6EJ',2),('Seaton Burn Services, Fisher Lane, Newcastle upon Tyne NE13 6BP',3),('Thirsk Town Centre, 26 Market Place, Thirsk YO7 1LB',4),('Whitby Town Centre, Station Square, Whitby YO21 1DX',5),('Whitby Town Centre, Station Square, Whitby YO21 1DX',6),('Thirsk Town Centre, 26 Market Place, Thirsk YO7 1LB',7),('Alnwick Town Centre, 19 Lagny Street, Alnwick, NE66 1LA',8);
/*!40000 ALTER TABLE `Orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Restaurants`
--

DROP TABLE IF EXISTS `Restaurants`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `Restaurants` (
  `restaurantAddress` varchar(100) NOT NULL,
  PRIMARY KEY (`restaurantAddress`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Restaurants`
--

LOCK TABLES `Restaurants` WRITE;
/*!40000 ALTER TABLE `Restaurants` DISABLE KEYS */;
INSERT INTO `Restaurants` VALUES ('Alnwick Town Centre, 19 Lagny Street, Alnwick, NE66 1LA'),('Newton Aycliffe, 4 Northfield Way, Newton Aycliffe, DL5 6EJ'),('Seaton Burn Services, Fisher Lane, Newcastle upon Tyne NE13 6BP'),('Thirsk Town Centre, 26 Market Place, Thirsk YO7 1LB'),('Whitby Town Centre, Station Square, Whitby YO21 1DX');
/*!40000 ALTER TABLE `Restaurants` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Stock`
--

DROP TABLE IF EXISTS `Stock`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `Stock` (
  `stockItem` varchar(100) NOT NULL,
  `unitSize` int(11) NOT NULL,
  `unitUnits` varchar(45) NOT NULL,
  `unitCost` decimal(10,2) NOT NULL,
  `typicalUnitsOrdered` int(11) NOT NULL,
  PRIMARY KEY (`stockItem`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Stock`
--

LOCK TABLES `Stock` WRITE;
/*!40000 ALTER TABLE `Stock` DISABLE KEYS */;
INSERT INTO `Stock` VALUES ('Cheese Slices',50,'Slices',2.95,10),('Chicken Breast Fillets',30,'Pieces',10.00,40),('Chicken Pieces',30,'Pieces',7.75,40),('Chicken strips',30,'Pieces',8.45,40),('Cola syrup',15000,'ml',6.50,3),('Hash Browns',25,'Pieces',7.50,16),('Mayonnaise',5000,'ml',10.00,2),('Mycoprotein based meat substitute Southern fried burger',18,'Pieces',3.00,5),('Mycoprotein based meat substitute Southern fried Strips',18,'Pieces',4.00,5),('Sesame Seed Buns',20,'Buns',5.45,44),('Shredded iceberg lettuce',500,'g',1.25,10),('Uncooked French Fries',1200,'g',3.00,60);
/*!40000 ALTER TABLE `Stock` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `StockOrders`
--

DROP TABLE IF EXISTS `StockOrders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `StockOrders` (
  `orderId` int(11) NOT NULL AUTO_INCREMENT,
  `orderStatus` char(20) DEFAULT NULL,
  `orderDateTime` datetime NOT NULL,
  `orderDeliveryDate` date NOT NULL,
  PRIMARY KEY (`orderId`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `StockOrders`
--

LOCK TABLES `StockOrders` WRITE;
/*!40000 ALTER TABLE `StockOrders` DISABLE KEYS */;
INSERT INTO `StockOrders` VALUES (1,'Pending','2019-05-12 13:13:13','2019-05-13'),(2,'Pending','2019-05-12 13:13:13','2019-05-13'),(3,'Pending','2019-05-12 13:13:13','2019-05-13'),(4,'Pending','2019-05-12 13:13:13','2019-05-13'),(5,'Pending','2019-05-12 13:13:13','2019-05-13'),(6,'Approved','2019-05-12 13:13:13','2019-05-13'),(7,'Approved','2019-05-12 13:13:13','2019-05-13'),(8,'Approved','2019-05-12 13:13:13','2019-05-13');
/*!40000 ALTER TABLE `StockOrders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Warehouse`
--

DROP TABLE IF EXISTS `Warehouse`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `Warehouse` (
  `warehouseAddress` varchar(100) NOT NULL,
  PRIMARY KEY (`warehouseAddress`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Warehouse`
--

LOCK TABLES `Warehouse` WRITE;
/*!40000 ALTER TABLE `Warehouse` DISABLE KEYS */;
INSERT INTO `Warehouse` VALUES ('2 Drum Rd, Chester le Street, DH2 1AB');
/*!40000 ALTER TABLE `Warehouse` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Within`
--

DROP TABLE IF EXISTS `Within`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `Within` (
  `restaurantAddress` varchar(100) NOT NULL,
  `stockItem` varchar(100) NOT NULL,
  `quantity` int(11) NOT NULL,
  `minQuantity` int(11) NOT NULL,
  PRIMARY KEY (`restaurantAddress`,`stockItem`),
  KEY `stockItem` (`stockItem`),
  CONSTRAINT `Within_ibfk_1` FOREIGN KEY (`restaurantAddress`) REFERENCES `Restaurants` (`restaurantAddress`),
  CONSTRAINT `Within_ibfk_2` FOREIGN KEY (`stockItem`) REFERENCES `Stock` (`stockItem`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Within`
--

LOCK TABLES `Within` WRITE;
/*!40000 ALTER TABLE `Within` DISABLE KEYS */;
INSERT INTO `Within` VALUES ('Alnwick Town Centre, 19 Lagny Street, Alnwick, NE66 1LA','Cheese Slices',0,100),('Alnwick Town Centre, 19 Lagny Street, Alnwick, NE66 1LA','Chicken Pieces',0,100),('Alnwick Town Centre, 19 Lagny Street, Alnwick, NE66 1LA','Chicken strips',0,100),('Alnwick Town Centre, 19 Lagny Street, Alnwick, NE66 1LA','Cola syrup',0,100),('Alnwick Town Centre, 19 Lagny Street, Alnwick, NE66 1LA','Hash Browns',0,100),('Alnwick Town Centre, 19 Lagny Street, Alnwick, NE66 1LA','Mayonnaise',0,100),('Alnwick Town Centre, 19 Lagny Street, Alnwick, NE66 1LA','Mycoprotein based meat substitute Southern fried burger',0,100),('Alnwick Town Centre, 19 Lagny Street, Alnwick, NE66 1LA','Mycoprotein based meat substitute Southern fried Strips',0,100),('Alnwick Town Centre, 19 Lagny Street, Alnwick, NE66 1LA','Sesame Seed Buns',0,100),('Alnwick Town Centre, 19 Lagny Street, Alnwick, NE66 1LA','Shredded iceberg lettuce',0,100),('Alnwick Town Centre, 19 Lagny Street, Alnwick, NE66 1LA','Uncooked French Fries',0,100),('Newton Aycliffe, 4 Northfield Way, Newton Aycliffe, DL5 6EJ','Cheese Slices',0,100),('Newton Aycliffe, 4 Northfield Way, Newton Aycliffe, DL5 6EJ','Chicken Pieces',0,100),('Newton Aycliffe, 4 Northfield Way, Newton Aycliffe, DL5 6EJ','Chicken strips',0,100),('Newton Aycliffe, 4 Northfield Way, Newton Aycliffe, DL5 6EJ','Cola syrup',0,100),('Newton Aycliffe, 4 Northfield Way, Newton Aycliffe, DL5 6EJ','Hash Browns',0,100),('Newton Aycliffe, 4 Northfield Way, Newton Aycliffe, DL5 6EJ','Mayonnaise',0,100),('Newton Aycliffe, 4 Northfield Way, Newton Aycliffe, DL5 6EJ','Mycoprotein based meat substitute Southern fried burger',0,100),('Newton Aycliffe, 4 Northfield Way, Newton Aycliffe, DL5 6EJ','Mycoprotein based meat substitute Southern fried Strips',0,100),('Newton Aycliffe, 4 Northfield Way, Newton Aycliffe, DL5 6EJ','Sesame Seed Buns',0,100),('Newton Aycliffe, 4 Northfield Way, Newton Aycliffe, DL5 6EJ','Shredded iceberg lettuce',0,100),('Newton Aycliffe, 4 Northfield Way, Newton Aycliffe, DL5 6EJ','Uncooked French Fries',0,100),('Seaton Burn Services, Fisher Lane, Newcastle upon Tyne NE13 6BP','Cheese Slices',0,100),('Seaton Burn Services, Fisher Lane, Newcastle upon Tyne NE13 6BP','Chicken Pieces',0,100),('Seaton Burn Services, Fisher Lane, Newcastle upon Tyne NE13 6BP','Chicken strips',0,100),('Seaton Burn Services, Fisher Lane, Newcastle upon Tyne NE13 6BP','Cola syrup',0,100),('Seaton Burn Services, Fisher Lane, Newcastle upon Tyne NE13 6BP','Hash Browns',0,100),('Seaton Burn Services, Fisher Lane, Newcastle upon Tyne NE13 6BP','Mayonnaise',0,100),('Seaton Burn Services, Fisher Lane, Newcastle upon Tyne NE13 6BP','Mycoprotein based meat substitute Southern fried burger',0,100),('Seaton Burn Services, Fisher Lane, Newcastle upon Tyne NE13 6BP','Mycoprotein based meat substitute Southern fried Strips',0,100),('Seaton Burn Services, Fisher Lane, Newcastle upon Tyne NE13 6BP','Sesame Seed Buns',0,100),('Seaton Burn Services, Fisher Lane, Newcastle upon Tyne NE13 6BP','Shredded iceberg lettuce',0,100),('Seaton Burn Services, Fisher Lane, Newcastle upon Tyne NE13 6BP','Uncooked French Fries',0,100),('Thirsk Town Centre, 26 Market Place, Thirsk YO7 1LB','Cheese Slices',0,100),('Thirsk Town Centre, 26 Market Place, Thirsk YO7 1LB','Chicken Pieces',0,100),('Thirsk Town Centre, 26 Market Place, Thirsk YO7 1LB','Chicken strips',0,100),('Thirsk Town Centre, 26 Market Place, Thirsk YO7 1LB','Cola syrup',0,100),('Thirsk Town Centre, 26 Market Place, Thirsk YO7 1LB','Hash Browns',0,100),('Thirsk Town Centre, 26 Market Place, Thirsk YO7 1LB','Mayonnaise',0,100),('Thirsk Town Centre, 26 Market Place, Thirsk YO7 1LB','Mycoprotein based meat substitute Southern fried burger',0,100),('Thirsk Town Centre, 26 Market Place, Thirsk YO7 1LB','Mycoprotein based meat substitute Southern fried Strips',0,100),('Thirsk Town Centre, 26 Market Place, Thirsk YO7 1LB','Sesame Seed Buns',0,100),('Thirsk Town Centre, 26 Market Place, Thirsk YO7 1LB','Shredded iceberg lettuce',0,100),('Thirsk Town Centre, 26 Market Place, Thirsk YO7 1LB','Uncooked French Fries',0,100),('Whitby Town Centre, Station Square, Whitby YO21 1DX','Cheese Slices',0,100),('Whitby Town Centre, Station Square, Whitby YO21 1DX','Chicken Pieces',0,100),('Whitby Town Centre, Station Square, Whitby YO21 1DX','Chicken strips',0,100),('Whitby Town Centre, Station Square, Whitby YO21 1DX','Cola syrup',0,100),('Whitby Town Centre, Station Square, Whitby YO21 1DX','Hash Browns',0,100),('Whitby Town Centre, Station Square, Whitby YO21 1DX','Mayonnaise',0,100),('Whitby Town Centre, Station Square, Whitby YO21 1DX','Mycoprotein based meat substitute Southern fried burger ',0,100),('Whitby Town Centre, Station Square, Whitby YO21 1DX','Mycoprotein based meat substitute Southern fried Strips',0,100),('Whitby Town Centre, Station Square, Whitby YO21 1DX','Sesame Seed Buns',0,100),('Whitby Town Centre, Station Square, Whitby YO21 1DX','Shredded iceberg lettuce',0,100),('Whitby Town Centre, Station Square, Whitby YO21 1DX','Uncooked French Fries',0,100);
/*!40000 ALTER TABLE `Within` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `purchaseHistory`
--

DROP TABLE IF EXISTS `purchaseHistory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `purchaseHistory` (
  `purchaseId` int(11) NOT NULL AUTO_INCREMENT,
  `mealId` varchar(200) NOT NULL,
  `dateTime` datetime NOT NULL,
  `restaurantAddress` varchar(100) NOT NULL,
  PRIMARY KEY (`purchaseId`),
  KEY `restaurantAddress` (`restaurantAddress`),
  KEY `purchaseHistory_ibfk_1` (`mealId`),
  CONSTRAINT `purchaseHistory_ibfk_1` FOREIGN KEY (`mealId`) REFERENCES `Meals` (`mealId`),
  CONSTRAINT `purchaseHistory_ibfk_2` FOREIGN KEY (`restaurantAddress`) REFERENCES `Restaurants` (`restaurantAddress`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `purchaseHistory`
--

LOCK TABLES `purchaseHistory` WRITE;
/*!40000 ALTER TABLE `purchaseHistory` DISABLE KEYS */;
/*!40000 ALTER TABLE `purchaseHistory` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-05-13 13:10:23
