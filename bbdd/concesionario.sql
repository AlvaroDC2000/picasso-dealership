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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `repair_order`
--

LOCK TABLES `repair_order` WRITE;
/*!40000 ALTER TABLE `repair_order` DISABLE KEYS */;
INSERT INTO `repair_order` VALUES (1,3,7,2,4,'FINISHED',NULL,NULL,'2026-01-11 13:25:24','2026-01-11 13:25:25','Oil change'),(2,3,3,2,4,'FINISHED',NULL,NULL,'2026-01-11 23:30:08','2026-01-11 23:30:24','Engine fail'),(3,5,6,2,4,'ASSIGNED',NULL,NULL,NULL,NULL,'Light fails'),(4,5,5,2,5,'ASSIGNED',NULL,NULL,NULL,NULL,'Engine and oil');
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
INSERT INTO `user` VALUES (1,'owner','1234','Juan García García',1,1,4,NULL),(2,'jefe.malaga','1234','Francisco Javier Morales',1,1,2,NULL),(3,'jefe.marbella','1234','Antonio Ruiz Fernández',1,2,2,NULL),(4,'mec.alex','1234','Alejandro Sánchez López',1,1,1,'Engine, electrical'),(5,'mec.marta','1234','Marta García Pérez',1,1,1,'Brakes, engine'),(6,'mec.david','1234','David Romero Díaz',1,1,1,'Engine'),(7,'mec.lucia','1234','Lucía Torres Martín',1,1,1,'Brakes, engine'),(8,'mec.sergio','1234','Sergio Navarro Gómez',1,2,1,NULL),(9,'mec.paula','1234','Paula Jiménez Ruiz',1,2,1,NULL),(10,'mec.ivan','1234','Iván Ortega Castillo',1,3,1,NULL),(11,'mec.sofia','1234','Sofía Molina Herrera',1,3,1,NULL),(12,'ventas.laura','1234','Laura Campos Vega',1,1,3,NULL),(13,'ventas.adrian','1234','Adrián Blanco Núñez',1,2,3,NULL);
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
  `brand` varchar(80) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `model` varchar(80) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `category_id` int DEFAULT NULL,
  `entry_date` date DEFAULT NULL,
  `current_dealership_id` int DEFAULT NULL,
  `status` varchar(40) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `vin` (`vin`),
  KEY `category_id` (`category_id`),
  KEY `current_dealership_id` (`current_dealership_id`),
  CONSTRAINT `vehicle_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `vehicle_category` (`id`),
  CONSTRAINT `vehicle_ibfk_2` FOREIGN KEY (`current_dealership_id`) REFERENCES `dealership` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vehicle`
--

LOCK TABLES `vehicle` WRITE;
/*!40000 ALTER TABLE `vehicle` DISABLE KEYS */;
INSERT INTO `vehicle` VALUES (1,'VIN001','SEAT','Ibiza 1.0 TSI 2020',5,'2026-01-01',1,'IN_REPAIR'),(2,'VIN002','SEAT','León 1.5 TSI 2019',5,'2026-01-02',1,'IN_REPAIR'),(3,'VIN003','Volkswagen','Golf 1.5 TSI 2018',5,'2026-01-03',1,'IN_REPAIR'),(4,'VIN004','Renault','Clio 1.2 2018',5,'2026-01-04',1,'IN_REPAIR'),(5,'VIN005','Renault','Megane 1.3 TCe 2020',1,'2026-01-05',1,'IN_REPAIR'),(6,'VIN006','Peugeot','208 PureTech 2021',5,'2026-01-06',1,'IN_REPAIR'),(7,'VIN007','Peugeot','308 1.6 BlueHDi',1,'2026-01-07',1,'IN_REPAIR'),(8,'VIN008','Toyota','Corolla Hybrid 2020',6,'2026-01-08',1,'IN_REPAIR'),(9,'VIN009','Toyota','Yaris Hybrid 2021',6,'2026-01-09',1,'IN_REPAIR'),(10,'VIN010','Hyundai','i30 1.4 2019',5,'2026-01-10',1,'IN_REPAIR'),(11,'VIN011','BMW','320d 2017',1,'2026-01-01',2,'IN_REPAIR'),(12,'VIN012','BMW','X1 2.0d 2018',2,'2026-01-02',2,'IN_REPAIR'),(13,'VIN013','Audi','A3 1.6 TDI',5,'2026-01-03',2,'IN_REPAIR'),(14,'VIN014','Audi','A4 2.0 TDI',1,'2026-01-04',2,'IN_REPAIR'),(15,'VIN015','Mercedes','A200 2019',5,'2026-01-05',2,'IN_REPAIR'),(16,'VIN016','Mercedes','C220d 2018',1,'2026-01-06',2,'IN_REPAIR'),(17,'VIN017','Ford','Focus EcoBoost 2020',5,'2026-01-07',2,'IN_REPAIR'),(18,'VIN018','Ford','Kuga 1.5 2019',2,'2026-01-08',2,'IN_REPAIR'),(19,'VIN019','Nissan','Qashqai dCi 2018',2,'2026-01-09',2,'IN_REPAIR'),(20,'VIN020','Mazda','CX-5 2.0 2019',2,'2026-01-10',2,'IN_REPAIR'),(21,'VIN021','Opel','Corsa 1.2 2021',5,'2026-01-01',3,'IN_REPAIR'),(22,'VIN022','Opel','Astra 1.6 CDTI',1,'2026-01-02',3,'IN_REPAIR'),(23,'VIN023','Citroën','C3 1.2 2019',5,'2026-01-03',3,'IN_REPAIR'),(24,'VIN024','Citroën','Berlingo BlueHDi',3,'2026-01-04',3,'IN_REPAIR'),(25,'VIN025','Kia','Ceed 1.4 T-GDi',5,'2026-01-05',3,'IN_REPAIR'),(26,'VIN026','Kia','Sportage 1.6',2,'2026-01-06',3,'IN_REPAIR'),(27,'VIN027','Skoda','Octavia 1.5 TSI',1,'2026-01-07',3,'IN_REPAIR'),(28,'VIN028','Skoda','Kamiq 1.0 TSI',2,'2026-01-08',3,'IN_REPAIR'),(29,'VIN029','Tesla','Model 3 2022',6,'2026-01-09',3,'IN_REPAIR'),(30,'VIN030','Cupra','Formentor 2.0',2,'2026-01-10',3,'IN_REPAIR'),(31,'VIN031','Dacia','Sandero 2020',5,'2026-01-01',1,'IN_REPAIR'),(32,'VIN032','Dacia','Duster 2019',2,'2026-01-02',1,'IN_REPAIR'),(33,'VIN033','Fiat','500 Hybrid 2021',6,'2026-01-03',1,'IN_REPAIR'),(34,'VIN034','Fiat','Tipo 1.4 2018',1,'2026-01-04',1,'IN_REPAIR'),(35,'VIN035','Honda','Civic 1.5 2019',1,'2026-01-05',1,'IN_REPAIR'),(36,'VIN036','Honda','CR-V Hybrid 2020',6,'2026-01-06',1,'IN_REPAIR'),(37,'VIN037','Volvo','XC40 T3 2021',2,'2026-01-07',2,'IN_REPAIR'),(38,'VIN038','Volvo','V40 D2 2018',1,'2026-01-08',2,'IN_REPAIR'),(39,'VIN039','Mini','Cooper 1.5 2019',5,'2026-01-09',2,'IN_REPAIR'),(40,'VIN040','Mini','Countryman 2020',2,'2026-01-10',2,'IN_REPAIR'),(41,'VIN041','Jeep','Renegade 1.3',2,'2026-01-01',3,'IN_REPAIR'),(42,'VIN042','Jeep','Compass 1.6',2,'2026-01-02',3,'IN_REPAIR'),(43,'VIN043','Suzuki','Vitara 1.4',2,'2026-01-03',3,'IN_REPAIR'),(44,'VIN044','Suzuki','Swift 1.2',5,'2026-01-04',3,'IN_REPAIR'),(45,'VIN045','Alfa Romeo','Giulietta 1.6',1,'2026-01-05',3,'IN_REPAIR'),(46,'VIN046','Alfa Romeo','Stelvio 2.0',2,'2026-01-06',3,'IN_REPAIR'),(47,'VIN047','Hyundai','Kona Hybrid',6,'2026-01-07',3,'IN_REPAIR'),(48,'VIN048','Hyundai','i20 1.0',5,'2026-01-08',3,'IN_REPAIR'),(49,'VIN049','Toyota','RAV4 Hybrid',6,'2026-01-09',3,'IN_REPAIR'),(50,'VIN050','Toyota','C-HR Hybrid',6,'2026-01-10',3,'IN_REPAIR');
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

-- Dump completed on 2026-01-12  9:59:28
