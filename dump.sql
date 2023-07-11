-- MySQL dump 10.13  Distrib 8.0.32, for Win64 (x86_64)
--
-- Host: localhost    Database: onlineauctions
-- ------------------------------------------------------
-- Server version	8.0.32

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `auction`
--

DROP TABLE IF EXISTS `auction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `auction` (
  `auctionid` int unsigned NOT NULL AUTO_INCREMENT,
  `creatorid` int unsigned NOT NULL,
  `isopen` tinyint NOT NULL,
  `startingbid` decimal(15,2) unsigned NOT NULL,
  `deadline` timestamp NOT NULL,
  `minraise` decimal(15,2) unsigned NOT NULL,
  PRIMARY KEY (`auctionid`),
  UNIQUE KEY `auctionid_UNIQUE` (`auctionid`),
  KEY `creatorid_idx` (`creatorid`),
  CONSTRAINT `creatorid` FOREIGN KEY (`creatorid`) REFERENCES `user` (`userid`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `auction`
--

LOCK TABLES `auction` WRITE;
/*!40000 ALTER TABLE `auction` DISABLE KEYS */;
INSERT INTO `auction` VALUES (1,1,1,4.00,'2025-05-25 15:27:00',1.00),(2,1,1,80.00,'2023-12-31 23:00:00',10.00),(3,1,0,100.00,'2023-05-24 10:59:59',5.00),(4,2,1,50.00,'2023-12-31 23:00:00',8.00),(5,2,1,17.00,'2025-08-12 18:57:00',17.00),(6,3,1,50.00,'2023-09-09 10:00:00',2.50),(22,1,1,10.00,'2023-07-27 05:32:00',1.00),(23,1,1,10.00,'2023-07-21 05:52:00',1.00);
/*!40000 ALTER TABLE `auction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bid`
--

DROP TABLE IF EXISTS `bid`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bid` (
  `bidid` int unsigned NOT NULL AUTO_INCREMENT,
  `bidderid` int unsigned NOT NULL,
  `auctionid` int unsigned NOT NULL,
  `date` timestamp NOT NULL,
  `price` decimal(15,2) unsigned NOT NULL,
  PRIMARY KEY (`bidid`),
  UNIQUE KEY `bidid_UNIQUE` (`bidid`),
  KEY `auctionid_idx` (`auctionid`),
  KEY `bidderid_idx` (`bidderid`),
  CONSTRAINT `auctionid` FOREIGN KEY (`auctionid`) REFERENCES `auction` (`auctionid`) ON UPDATE CASCADE,
  CONSTRAINT `bidderid` FOREIGN KEY (`bidderid`) REFERENCES `user` (`userid`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=66 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bid`
--

LOCK TABLES `bid` WRITE;
/*!40000 ALTER TABLE `bid` DISABLE KEYS */;
INSERT INTO `bid` VALUES (1,2,1,'2023-05-24 16:56:00',33.99),(2,3,2,'2023-05-24 16:56:00',170.00),(3,5,3,'2023-04-27 10:00:00',1000.99),(4,4,1,'2023-05-27 18:32:00',55.00),(5,3,1,'2023-05-28 11:36:42',75.00),(6,5,2,'2023-05-28 12:09:04',190.00),(7,4,2,'2023-05-28 12:11:49',205.00),(8,5,2,'2023-05-28 12:15:51',215.00),(9,5,2,'2023-05-28 12:16:12',230.00),(10,3,1,'2023-05-28 12:20:38',80.00),(11,5,1,'2023-06-02 11:36:45',81.00),(12,5,1,'2023-07-03 13:46:41',88.00),(13,5,2,'2023-07-05 12:52:10',240.00),(16,4,2,'2023-07-08 17:10:01',265.99),(17,5,2,'2023-07-08 17:10:20',276.00),(63,5,22,'2023-07-11 07:33:05',10.00),(64,5,1,'2023-07-11 07:50:48',90.00),(65,5,1,'2023-07-11 07:59:37',200.00);
/*!40000 ALTER TABLE `bid` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `item`
--

DROP TABLE IF EXISTS `item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `item` (
  `itemid` int unsigned NOT NULL AUTO_INCREMENT,
  `auctionerid` int unsigned NOT NULL,
  `name` varchar(45) NOT NULL,
  `price` decimal(10,2) unsigned NOT NULL,
  `description` varchar(500) NOT NULL,
  `picture` varchar(200) NOT NULL,
  `isonauction` tinyint NOT NULL DEFAULT '0',
  `auctionidin` int DEFAULT NULL,
  `isalreadysold` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`itemid`),
  UNIQUE KEY `itemid_UNIQUE` (`itemid`),
  KEY `auctionerid_idx` (`auctionerid`),
  CONSTRAINT `auctionerid` FOREIGN KEY (`auctionerid`) REFERENCES `user` (`userid`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `item`
--

LOCK TABLES `item` WRITE;
/*!40000 ALTER TABLE `item` DISABLE KEYS */;
INSERT INTO `item` VALUES (1,1,'Quaderno',1.00,'Quaderno da 80 pagine a quadretti 5mm','quaderno.png',1,1,0),(2,1,'Penna',2.00,'Penna strafiga, comprala','penna.jpg',1,1,0),(3,1,'Matita',1.00,'Matita HB, una di quelle gialle e nere tipo le api. La mina non si spezza mai.','matita.jpg',1,1,0),(4,1,'My Shelfie',60.00,'Board Game di My Shelfie. Utilissimo se stai facento il progetto di Ingegneria del Software','shelf.png',0,3,1),(5,1,'CSSbook',40.00,'Libro di CSS. Sembra fatto apposta per le persone che si chiamano Jack.','css.jpg',0,3,1),(6,1,'Autografo Mario Draghi',5.00,'Vendo Autografo di Mario Draghi a 5 euro.','euro.jpg',1,2,0),(7,1,'Naso Rosso',1.25,'Componente del set da Clown. Da abbinare al Cappello Elicottero e la parrucca','naso.jpg',0,NULL,0),(8,1,'Cappello ad Elicottero',7.00,'Componente del set da Clown. Da abbinare al naso e la parrucca.','Cappello.jpg',0,NULL,0),(28,1,'Vasi',10.00,'Sono dei vasi','vasi.jpg',1,22,0),(29,1,'Dipinto',10.00,'b wefrjhknfmdwnb fvefdwks,mnvf brhjekmswd nvrfhjkadmsn vrefukikmcj sh','dipinto.jpg',1,23,0);
/*!40000 ALTER TABLE `item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `userid` int unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(45) NOT NULL,
  `name` varchar(45) NOT NULL,
  `surname` varchar(45) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(45) NOT NULL,
  `city` varchar(45) NOT NULL,
  `street` varchar(45) NOT NULL,
  `number` int unsigned NOT NULL,
  PRIMARY KEY (`userid`),
  UNIQUE KEY `userid_UNIQUE` (`userid`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'benels','Francesco','Benelle','frabenelle@gmail.com','1234','Desio','Via A',2),(2,'andew','Andrea','Andrei','andreaandrei@gmail.com','1234','Milano','Via C',7),(3,'goat','Denzel','Dumfries','ddumfries@gmail.com','1234','Amsterdam','Via Leopardi',3),(4,'carlos','Joaquin','Correa','joaquincorrea@gmail.com','1234','BuenosAires','Via DalCampo',11),(5,'jackb','Giacomo','Ballabio','jackb@gmail.com','1234','Como','Via Anzano',22),(6,'briefing','Bhristian','Ciffi','cristianbriefing@gmail.com','schuwzbravra','Lecco','Via dei Sbrugni',6),(7,'charlene','Chiara','Auriemma','aurigemmaclaris@gmail.com','1234','Foligno','Via Volta',86),(8,'ceo','Michele','Cavicchioli','sonoilceo@gmail.com','1234','Milano','Via Questa',1),(9,'ilboglions','Matteo','Boglioni','boglioniswag@gmail.com','1234','Bassano del Grappa','Via CarloCarlotti',69);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-07-11 10:18:27
