-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: localhost    Database: concesionario
-- ------------------------------------------------------
-- Server version	8.0.41

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
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer` (
  `id` int NOT NULL AUTO_INCREMENT,
  `dni` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `first_name` varchar(80) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `last_name` varchar(80) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phone` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(120) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `dni` (`dni`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer`
--

LOCK TABLES `customer` WRITE;
/*!40000 ALTER TABLE `customer` DISABLE KEYS */;
INSERT INTO `customer` VALUES (1,'11111111A','Juan','Pérez Gómez','600111111','juan.perez@gmail.com'),(2,'22222222B','Ana','Martín López','600222222','ana.martin@hotmail.com'),(3,'33333333C','Carmen','Ruiz Sánchez','600333333','carmen.ruiz@yahoo.es'),(4,'44444444D','Manuel','García Fernández','600444444','manuel.garcia@gmail.com'),(5,'55555555E','Lucía','Navarro Torres','600555555','lucia.navarro@outlook.com'),(6,'66666666F','Sergio','Romero Díaz','600666666','sergio.romero@gmail.com'),(7,'77777777G','Paula','Molina Herrera','600777777','paula.molina@yahoo.es'),(8,'88888888H','Javier','Ortega Castillo','600888888','javier.ortega@hotmail.com');
/*!40000 ALTER TABLE `customer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dealership`
--

DROP TABLE IF EXISTS `dealership`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dealership` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `city` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dealership`
--

LOCK TABLES `dealership` WRITE;
/*!40000 ALTER TABLE `dealership` DISABLE KEYS */;
INSERT INTO `dealership` VALUES (1,'Concesionario Picasso','Málaga'),(2,'Picasso Motor Costa','Marbella'),(3,'Picasso Motor Centro','Sevilla');
/*!40000 ALTER TABLE `dealership` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `repair_order`
--

DROP TABLE IF EXISTS `repair_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `repair_order` (
  `id` int NOT NULL AUTO_INCREMENT,
  `vehicle_id` int NOT NULL,
  `customer_id` int NOT NULL,
  `created_by_boss_id` int NOT NULL,
  `assigned_mechanic_id` int DEFAULT NULL,
  `status` varchar(40) COLLATE utf8mb4_unicode_ci NOT NULL,
  `estimated_hours` decimal(5,2) DEFAULT NULL,
  `estimated_budget` decimal(10,2) DEFAULT NULL,
  `start_at` datetime DEFAULT NULL,
  `end_at` datetime DEFAULT NULL,
  `notes` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id`),
  KEY `vehicle_id` (`vehicle_id`),
  KEY `customer_id` (`customer_id`),
  KEY `created_by_boss_id` (`created_by_boss_id`),
  KEY `assigned_mechanic_id` (`assigned_mechanic_id`),
  CONSTRAINT `repair_order_ibfk_1` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicle` (`id`),
  CONSTRAINT `repair_order_ibfk_2` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`),
  CONSTRAINT `repair_order_ibfk_3` FOREIGN KEY (`created_by_boss_id`) REFERENCES `user` (`id`),
  CONSTRAINT `repair_order_ibfk_4` FOREIGN KEY (`assigned_mechanic_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `repair_order`
--

LOCK TABLES `repair_order` WRITE;
/*!40000 ALTER TABLE `repair_order` DISABLE KEYS */;
INSERT INTO `repair_order` VALUES (1,3,7,2,4,'FINISHED',NULL,NULL,'2026-01-11 13:25:24','2026-01-11 13:25:25','Oil change'),(2,3,3,2,4,'FINISHED',NULL,NULL,'2026-01-11 23:30:08','2026-01-11 23:30:24','Engine fail'),(3,5,6,2,4,'IN_PROGRESS',NULL,NULL,'2026-01-12 10:34:48',NULL,'Light fails'),(4,5,5,2,5,'ASSIGNED',NULL,NULL,NULL,NULL,'Engine and oil'),(5,9,6,2,4,'FINISHED',NULL,NULL,'2026-01-12 10:33:43','2026-01-12 10:33:49','Oil and engine'),(6,2,3,2,4,'ASSIGNED',NULL,NULL,NULL,NULL,'oil and engine'),(7,3,3,2,4,'ASSIGNED',NULL,NULL,NULL,NULL,'break'),(8,22,6,2,4,'ASSIGNED',NULL,NULL,NULL,NULL,'engine'),(9,24,7,2,4,'ASSIGNED',NULL,NULL,NULL,NULL,'engine'),(10,8,7,2,4,'ASSIGNED',NULL,NULL,NULL,NULL,'engine'),(11,3,4,2,4,'ASSIGNED',NULL,NULL,NULL,NULL,'oil');
/*!40000 ALTER TABLE `repair_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (2,'CHIEF_MECHANIC'),(1,'MECHANIC'),(4,'OWNER'),(3,'SALES');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sale`
--

DROP TABLE IF EXISTS `sale`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sale` (
  `id` int NOT NULL AUTO_INCREMENT,
  `proposal_id` int NOT NULL,
  `customer_id` int NOT NULL,
  `vehicle_id` int NOT NULL,
  `seller_user_id` int NOT NULL,
  `dealership_id` int NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `sale_date` date NOT NULL,
  `notes` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_sale_proposal` (`proposal_id`),
  KEY `idx_sale_customer` (`customer_id`),
  KEY `idx_sale_vehicle` (`vehicle_id`),
  KEY `idx_sale_seller` (`seller_user_id`),
  KEY `idx_sale_dealership` (`dealership_id`),
  KEY `idx_sale_date` (`sale_date`),
  CONSTRAINT `fk_sale_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`),
  CONSTRAINT `fk_sale_dealership` FOREIGN KEY (`dealership_id`) REFERENCES `dealership` (`id`),
  CONSTRAINT `fk_sale_proposal` FOREIGN KEY (`proposal_id`) REFERENCES `sale_proposal` (`id`),
  CONSTRAINT `fk_sale_seller` FOREIGN KEY (`seller_user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_sale_vehicle` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicle` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sale`
--

LOCK TABLES `sale` WRITE;
/*!40000 ALTER TABLE `sale` DISABLE KEYS */;
INSERT INTO `sale` VALUES (1,1,1,1,12,1,10000.00,'2026-01-25','Car in perfect condition','2026-01-25 17:57:20');
/*!40000 ALTER TABLE `sale` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sale_proposal`
--

DROP TABLE IF EXISTS `sale_proposal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sale_proposal` (
  `id` int NOT NULL AUTO_INCREMENT,
  `customer_id` int NOT NULL,
  `vehicle_id` int NOT NULL,
  `seller_user_id` int NOT NULL,
  `dealership_id` int NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `notes` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `valid_until` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_sale_proposal_customer` (`customer_id`),
  KEY `idx_sale_proposal_vehicle` (`vehicle_id`),
  KEY `idx_sale_proposal_seller` (`seller_user_id`),
  KEY `idx_sale_proposal_dealership` (`dealership_id`),
  KEY `idx_sale_proposal_status` (`status`),
  CONSTRAINT `fk_sale_proposal_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`),
  CONSTRAINT `fk_sale_proposal_dealership` FOREIGN KEY (`dealership_id`) REFERENCES `dealership` (`id`),
  CONSTRAINT `fk_sale_proposal_seller` FOREIGN KEY (`seller_user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_sale_proposal_vehicle` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicle` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sale_proposal`
--

LOCK TABLES `sale_proposal` WRITE;
/*!40000 ALTER TABLE `sale_proposal` DISABLE KEYS */;
INSERT INTO `sale_proposal` VALUES (1,1,1,12,1,10000.00,'Car in perfect condition','ACCEPTED','2026-01-25 17:57:20','2026-01-25 17:57:20','2026-02-09'),(2,2,2,12,1,11000.00,'Includes 1 year warranty','ACTIVE','2026-01-25 17:57:20',NULL,'2026-02-09');
/*!40000 ALTER TABLE `sale_proposal` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password_hash` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `full_name` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `dealership_id` int NOT NULL,
  `role_id` int NOT NULL,
  `skills` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  KEY `dealership_id` (`dealership_id`),
  KEY `role_id` (`role_id`),
  CONSTRAINT `user_ibfk_1` FOREIGN KEY (`dealership_id`) REFERENCES `dealership` (`id`),
  CONSTRAINT `user_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'owner','1234','Juan García García',1,1,4,NULL),(2,'jefe.malaga','1234','Francisco Javier Morales',1,1,2,NULL),(3,'jefe.marbella','1234','Antonio Ruiz Fernández',1,2,2,NULL),(4,'mec.alex','1234','Alejandro Sánchez López',1,1,1,'Engine, electrical, break,oil'),(5,'mec.marta','1234','Marta García Pérez',1,1,1,'Brakes, engine'),(6,'mec.david','1234','David Romero Díaz',1,1,1,'Engine'),(7,'mec.lucia','1234','Lucía Torres Martín',1,1,1,'Brakes, engine'),(8,'mec.sergio','1234','Sergio Navarro Gómez',1,2,1,NULL),(9,'mec.paula','1234','Paula Jiménez Ruiz',1,2,1,NULL),(10,'mec.ivan','1234','Iván Ortega Castillo',1,3,1,NULL),(11,'mec.sofia','1234','Sofía Molina Herrera',1,3,1,NULL),(12,'ventas.laura','1234','Laura Campos Vega',1,1,3,NULL),(13,'ventas.adrian','1234','Adrián Blanco Núñez',1,2,3,NULL);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vehicle`
--

DROP TABLE IF EXISTS `vehicle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vehicle` (
  `id` int NOT NULL AUTO_INCREMENT,
  `vin` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `plate` varchar(15) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `brand` varchar(80) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `model` varchar(80) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `year` int DEFAULT NULL,
  `color` varchar(40) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mileage` int DEFAULT NULL,
  `notes` text COLLATE utf8mb4_unicode_ci,
  `category_id` int DEFAULT NULL,
  `entry_date` date DEFAULT NULL,
  `current_dealership_id` int DEFAULT NULL,
  `status` varchar(40) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sold_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `vin` (`vin`),
  UNIQUE KEY `ux_vehicle_plate` (`plate`),
  KEY `category_id` (`category_id`),
  KEY `current_dealership_id` (`current_dealership_id`),
  KEY `idx_vehicle_entry_date` (`entry_date`),
  KEY `idx_vehicle_status` (`status`),
  KEY `idx_vehicle_brand_model` (`brand`,`model`),
  CONSTRAINT `vehicle_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `vehicle_category` (`id`),
  CONSTRAINT `vehicle_ibfk_2` FOREIGN KEY (`current_dealership_id`) REFERENCES `dealership` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vehicle`
--

LOCK TABLES `vehicle` WRITE;
/*!40000 ALTER TABLE `vehicle` DISABLE KEYS */;
INSERT INTO `vehicle` VALUES (1,'VIN001','2354MCD','SEAT','Ibiza 1.0 TSI 2020',2017,'Black',68500,'Well-maintained vehicle, ideal for city driving.',5,'2026-01-01',1,'SOLD','2026-01-25 17:57:20'),(2,'VIN002','2154LCD','SEAT','León 1.5 TSI 2019',2018,'White',52000,'Clean interior, low fuel consumption.',5,'2026-01-02',1,'AVAILABLE',NULL),(3,'VIN003','2324MLD','Volkswagen','Golf 1.5 TSI 2018',2019,'Blue',41000,'Good condition, recent service.',5,'2026-01-03',1,'AVAILABLE',NULL),(4,'VIN004','2351NCD','Renault','Clio 1.2 2018',2017,'Red',79000,'Good overall condition.',5,'2026-01-04',1,'AVAILABLE',NULL),(5,'VIN005','0005MCD','Renault','Megane 1.3 TCe 2020',2020,'Silver',41500,'Vehicle currently in repair. Pending final inspection.',1,'2026-01-05',1,'AVAILABLE',NULL),(6,'VIN006','0006MCD','Peugeot','208 PureTech 2021',2021,'Black',43800,'Vehicle currently in repair. Pending final inspection.',5,'2026-01-06',1,'AVAILABLE',NULL),(7,'VIN007','0007MCD','Peugeot','308 1.6 BlueHDi',2018,'White',46100,'Vehicle currently in repair. Pending final inspection.',1,'2026-01-07',1,'AVAILABLE',NULL),(8,'VIN008','0008MCD','Toyota','Corolla Hybrid 2020',2020,'Blue',48400,'Vehicle currently in repair. Pending final inspection.',6,'2026-01-08',1,'IN_REPAIR',NULL),(9,'VIN009','0009MCD','Toyota','Yaris Hybrid 2021',2021,'Red',50700,'Vehicle currently in repair. Pending final inspection.',6,'2026-01-09',1,'IN_REPAIR',NULL),(10,'VIN010','0010MCD','Hyundai','i30 1.4 2019',2019,'Gray',53000,'Vehicle currently in repair. Pending final inspection.',5,'2026-01-10',1,'IN_REPAIR',NULL),(11,'VIN011','0011MCD','BMW','320d 2017',2017,'Silver',55300,'Vehicle currently in repair. Pending final inspection.',1,'2026-01-01',2,'IN_REPAIR',NULL),(12,'VIN012','0012MCD','BMW','X1 2.0d 2018',2018,'Black',57600,'Vehicle currently in repair. Pending final inspection.',2,'2026-01-02',2,'IN_REPAIR',NULL),(13,'VIN013','0013MCD','Audi','A3 1.6 TDI',2018,'White',59900,'Vehicle currently in repair. Pending final inspection.',5,'2026-01-03',2,'IN_REPAIR',NULL),(14,'VIN014','0014MCD','Audi','A4 2.0 TDI',2019,'Blue',62200,'Vehicle currently in repair. Pending final inspection.',1,'2026-01-04',2,'IN_REPAIR',NULL),(15,'VIN015','0015MCD','Mercedes','A200 2019',2019,'Red',64500,'Vehicle currently in repair. Pending final inspection.',5,'2026-01-05',2,'IN_REPAIR',NULL),(16,'VIN016','0016MCD','Mercedes','C220d 2018',2018,'Gray',66800,'Vehicle currently in repair. Pending final inspection.',1,'2026-01-06',2,'IN_REPAIR',NULL),(17,'VIN017','0017MCD','Ford','Focus EcoBoost 2020',2020,'Silver',69100,'Vehicle currently in repair. Pending final inspection.',5,'2026-01-07',2,'IN_REPAIR',NULL),(18,'VIN018','0018MCD','Ford','Kuga 1.5 2019',2019,'Black',71400,'Vehicle currently in repair. Pending final inspection.',2,'2026-01-08',2,'IN_REPAIR',NULL),(19,'VIN019','0019MCD','Nissan','Qashqai dCi 2018',2018,'White',73700,'Vehicle currently in repair. Pending final inspection.',2,'2026-01-09',2,'IN_REPAIR',NULL),(20,'VIN020','0020MCD','Mazda','CX-5 2.0 2019',2019,'Blue',76000,'Vehicle currently in repair. Pending final inspection.',2,'2026-01-10',2,'IN_REPAIR',NULL),(21,'VIN021','0021MCD','Opel','Corsa 1.2 2021',2021,'Red',78300,'Vehicle currently in repair. Pending final inspection.',5,'2026-01-01',3,'IN_REPAIR',NULL),(22,'VIN022','0022MCD','Opel','Astra 1.6 CDTI',2021,'Gray',80600,'Vehicle currently in repair. Pending final inspection.',1,'2026-01-02',3,'IN_REPAIR',NULL),(23,'VIN023','0023MCD','Citroën','C3 1.2 2019',2019,'Silver',82900,'Vehicle currently in repair. Pending final inspection.',5,'2026-01-03',3,'IN_REPAIR',NULL),(24,'VIN024','0024MCD','Citroën','Berlingo BlueHDi',2017,'Black',85200,'Vehicle currently in repair. Pending final inspection.',3,'2026-01-04',3,'IN_REPAIR',NULL),(25,'VIN025','0025MCD','Kia','Ceed 1.4 T-GDi',2018,'White',87500,'Vehicle currently in repair. Pending final inspection.',5,'2026-01-05',3,'IN_REPAIR',NULL),(26,'VIN026','0026MCD','Kia','Sportage 1.6',2019,'Blue',89800,'Vehicle currently in repair. Pending final inspection.',2,'2026-01-06',3,'IN_REPAIR',NULL),(27,'VIN027','0027MCD','Skoda','Octavia 1.5 TSI',2020,'Red',92100,'Vehicle currently in repair. Pending final inspection.',1,'2026-01-07',3,'IN_REPAIR',NULL),(28,'VIN028','0028MCD','Skoda','Kamiq 1.0 TSI',2021,'Gray',94400,'Vehicle currently in repair. Pending final inspection.',2,'2026-01-08',3,'IN_REPAIR',NULL),(29,'VIN029','0029MCD','Tesla','Model 3 2022',2022,'Silver',96700,'Vehicle currently in repair. Pending final inspection.',6,'2026-01-09',3,'IN_REPAIR',NULL),(30,'VIN030','0030MCD','Cupra','Formentor 2.0',2017,'Black',99000,'Vehicle currently in repair. Pending final inspection.',2,'2026-01-10',3,'IN_REPAIR',NULL),(31,'VIN031','0031MCD','Dacia','Sandero 2020',2020,'White',101300,'Vehicle currently in repair. Pending final inspection.',5,'2026-01-01',1,'IN_REPAIR',NULL),(32,'VIN032','0032MCD','Dacia','Duster 2019',2019,'Blue',103600,'Vehicle currently in repair. Pending final inspection.',2,'2026-01-02',1,'IN_REPAIR',NULL),(33,'VIN033','0033MCD','Fiat','500 Hybrid 2021',2021,'Red',105900,'Vehicle currently in repair. Pending final inspection.',6,'2026-01-03',1,'IN_REPAIR',NULL),(34,'VIN034','0034MCD','Fiat','Tipo 1.4 2018',2018,'Gray',108200,'Vehicle currently in repair. Pending final inspection.',1,'2026-01-04',1,'IN_REPAIR',NULL),(35,'VIN035','0035MCD','Honda','Civic 1.5 2019',2019,'Silver',110500,'Vehicle currently in repair. Pending final inspection.',1,'2026-01-05',1,'IN_REPAIR',NULL),(36,'VIN036','0036MCD','Honda','CR-V Hybrid 2020',2020,'Black',112800,'Vehicle currently in repair. Pending final inspection.',6,'2026-01-06',1,'IN_REPAIR',NULL),(37,'VIN037','0037MCD','Volvo','XC40 T3 2021',2021,'White',115100,'Vehicle currently in repair. Pending final inspection.',2,'2026-01-07',2,'IN_REPAIR',NULL),(38,'VIN038','0038MCD','Volvo','V40 D2 2018',2018,'Blue',117400,'Vehicle currently in repair. Pending final inspection.',1,'2026-01-08',2,'IN_REPAIR',NULL),(39,'VIN039','0039MCD','Mini','Cooper 1.5 2019',2019,'Red',119700,'Vehicle currently in repair. Pending final inspection.',5,'2026-01-09',2,'IN_REPAIR',NULL),(40,'VIN040','0040MCD','Mini','Countryman 2020',2020,'Gray',122000,'Vehicle currently in repair. Pending final inspection.',2,'2026-01-10',2,'IN_REPAIR',NULL),(41,'VIN041','0041MCD','Jeep','Renegade 1.3',2022,'Silver',124300,'Vehicle currently in repair. Pending final inspection.',2,'2026-01-01',3,'IN_REPAIR',NULL),(42,'VIN042','0042MCD','Jeep','Compass 1.6',2017,'Black',126600,'Vehicle currently in repair. Pending final inspection.',2,'2026-01-02',3,'IN_REPAIR',NULL),(43,'VIN043','0043MCD','Suzuki','Vitara 1.4',2018,'White',128900,'Vehicle currently in repair. Pending final inspection.',2,'2026-01-03',3,'IN_REPAIR',NULL),(44,'VIN044','0044MCD','Suzuki','Swift 1.2',2019,'Blue',131200,'Vehicle currently in repair. Pending final inspection.',5,'2026-01-04',3,'IN_REPAIR',NULL),(45,'VIN045','0045MCD','Alfa Romeo','Giulietta 1.6',2020,'Red',133500,'Vehicle currently in repair. Pending final inspection.',1,'2026-01-05',3,'IN_REPAIR',NULL),(46,'VIN046','0046MCD','Alfa Romeo','Stelvio 2.0',2021,'Gray',135800,'Vehicle currently in repair. Pending final inspection.',2,'2026-01-06',3,'IN_REPAIR',NULL),(47,'VIN047','0047MCD','Hyundai','Kona Hybrid',2022,'Silver',138100,'Vehicle currently in repair. Pending final inspection.',6,'2026-01-07',3,'IN_REPAIR',NULL),(48,'VIN048','0048MCD','Hyundai','i20 1.0',2017,'Black',140400,'Vehicle currently in repair. Pending final inspection.',5,'2026-01-08',3,'IN_REPAIR',NULL),(49,'VIN049','0049MCD','Toyota','RAV4 Hybrid',2018,'White',142700,'Vehicle currently in repair. Pending final inspection.',6,'2026-01-09',3,'IN_REPAIR',NULL),(50,'VIN050','0050MCD','Toyota','C-HR Hybrid',2019,'Blue',145000,'Vehicle currently in repair. Pending final inspection.',6,'2026-01-10',3,'IN_REPAIR',NULL);
/*!40000 ALTER TABLE `vehicle` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vehicle_category`
--

DROP TABLE IF EXISTS `vehicle_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vehicle_category` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(80) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vehicle_category`
--

LOCK TABLES `vehicle_category` WRITE;
/*!40000 ALTER TABLE `vehicle_category` DISABLE KEYS */;
INSERT INTO `vehicle_category` VALUES (1,'Turismo'),(2,'SUV'),(3,'Furgoneta'),(4,'Deportivo'),(5,'Compacto'),(6,'Híbrido/Eléctrico');
/*!40000 ALTER TABLE `vehicle_category` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-01-25 18:39:18
