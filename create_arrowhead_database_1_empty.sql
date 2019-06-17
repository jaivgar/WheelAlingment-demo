CREATE DATABASE  IF NOT EXISTS `arrowhead_test_cloud_1` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `arrowhead_test_cloud_1`;
-- MySQL dump 10.13  Distrib 5.7.17, for Win64 (x86_64)
--
-- Host: localhost    Database: arrowhead_test_cloud_1
-- ------------------------------------------------------
-- Server version	5.7.21-log

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
-- Table structure for table `arrowhead_cloud`
--

DROP TABLE IF EXISTS `arrowhead_cloud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `arrowhead_cloud` (
  `id` int(11) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `authentication_info` varchar(2047) DEFAULT NULL,
  `cloud_name` varchar(255) DEFAULT NULL,
  `gatekeeper_service_uri` varchar(255) DEFAULT NULL,
  `operator` varchar(255) DEFAULT NULL,
  `port` int(11) DEFAULT NULL,
  `is_secure` char(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK9cjou6d7x3w0pvnnb27bc4c4d` (`operator`,`cloud_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `arrowhead_cloud`
--

LOCK TABLES `arrowhead_cloud` WRITE;
/*!40000 ALTER TABLE `arrowhead_cloud` DISABLE KEYS */;
INSERT INTO `arrowhead_cloud` VALUES (1,'0.0.0.0','NotSetYet','LC_ERP','gatekeeper','VTC',8446,'N'),(2,'0.0.0.0','NotSetYet','LC_Virtual_Truck','gatekeeper','Factory',10446,'N');
/*!40000 ALTER TABLE `arrowhead_cloud` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `arrowhead_service`
--

DROP TABLE IF EXISTS `arrowhead_service`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `arrowhead_service` (
  `id` bigint(20) NOT NULL,
  `service_definition` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKg90gjpqpv7tpmy1eou5u4umyk` (`service_definition`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `arrowhead_service_interfaces`
--

DROP TABLE IF EXISTS `arrowhead_service_interfaces`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `arrowhead_service_interfaces` (
  `arrowhead_service_id` bigint(20) NOT NULL,
  `interfaces` varchar(255) DEFAULT NULL,
  KEY `FKsb09f6kft101e8rixhm5t53f3` (`arrowhead_service_id`),
  CONSTRAINT `FKsb09f6kft101e8rixhm5t53f3` FOREIGN KEY (`arrowhead_service_id`) REFERENCES `arrowhead_service` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `arrowhead_system`
--

DROP TABLE IF EXISTS `arrowhead_system`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `arrowhead_system` (
  `id` int(11) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `authentication_info` varchar(2047) DEFAULT NULL,
  `system_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK3rj1egf6gi1enagslqry0pkkl` (`system_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `broker`
--

DROP TABLE IF EXISTS `broker`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `broker` (
  `id` int(11) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `authentication_info` varchar(2047) DEFAULT NULL,
  `broker_name` varchar(255) DEFAULT NULL,
  `port` int(11) DEFAULT NULL,
  `is_secure` char(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK7dsnnaysnsjuho4drpldwouao` (`broker_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `broker` WRITE;
/*!40000 ALTER TABLE `broker` DISABLE KEYS */;
INSERT INTO `broker` VALUES (1,'mantis3.tmit.bme.hu','','insecure_broker',5672,'N'),(2,'mantis3.tmit.bme.hu','MIIC+DCCAeCgAwIBAgIEWlYfrjANBgkqhkiG9w0BAQsFADBHMSEwHwYDVQQKDBhBSVRJQSBJbnRlcm5hdGlvbmFsIEluYy4xCzAJBgNVBAYTAkhVMRUwEwYDVQQDDAxhcnJvd2hlYWQuZXUwHhcNMTgwMTEwMTQxNDA2WhcNMjgwMTEwMTQxNDA2WjA1MQswCQYDVQQGEwJIVTEmMCQGA1UEAwwddGVzdGJyb2tlci5haXRpYS5hcnJvd2hlYWQuZXUwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDRkYEc6KdAXmy1gvuSu4u6MIJbS9JlkdtXnsP4G/HWFx3LuObIAMzbBw3NOYCIhhFwVxjvf0ME0gTA+llw7zdY+7dUj+TJ3EBtfgvajfHm2IO71S1kZTCCTfFdbbtXMj8uf7hCEFLvKM4GUNe6i368xkVh5eBihCbm/F77jKt/tV/K73NB91dJBC290RJjrkq0mj5Hs4+WY1ezX/B1XR1iOzjs6ZmL5gxh1A7PqQHkbL7/Qotos3qzHUIqzUR1QlpJYgS/fjZfrLoJvrfqWvKsQ8sD0y5wxdO1QXOt2EDA0SUUjkJDkePujUsU5ljoXEOgYLGVQJoz+MGVrH4rSFepAgMBAAEwDQYJKoZIhvcNAQELBQADggEBAEJUFWWWMMNnogOYiWdH4rUNVESw8rj1oczkZg+h+oQV4Qg6GxXFr9qL5LUOlcDRalJbWjd8yJBtQDIT7A2AuCQjLocgF1FZDa8nWcPkYNr7h4QX7E/7PNqAghjARSVaycMDtqaVCB0RlmcYMjreFjM71kRfHNcMOKLpdIMPhpfr2MC8E7EG6zfK0zsN3+qgZizqfR7Q8f6S0T8srIMuvBjk2h1aiM13ftu1/cn/d2RMAom46Mh1Z3qwhucO58BMwHzHJX24UE9xGWgOW3u/OrHNMmhzmNvlTNpbu3hZWoVMKnkWM3PVgGJSSJ2LlMBqO3uaOo1rdQ9WwPORST/Urgc=','secure_broker',5671,'Y');
/*!40000 ALTER TABLE `broker` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `event_filter`
--

DROP TABLE IF EXISTS `event_filter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `event_filter` (
  `id` int(11) NOT NULL,
  `end_date` datetime DEFAULT NULL,
  `event_type` varchar(255) DEFAULT NULL,
  `match_metadata` char(1) DEFAULT NULL,
  `notify_uri` varchar(255) DEFAULT NULL,
  `port` int(11) DEFAULT NULL,
  `start_date` datetime DEFAULT NULL,
  `consumer_system_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKbkos27fkducgbn6rxqty2k6n1` (`event_type`,`consumer_system_id`),
  KEY `FK8k1vieqrr0cxw4x0ubocsrrpo` (`consumer_system_id`),
  CONSTRAINT `FK8k1vieqrr0cxw4x0ubocsrrpo` FOREIGN KEY (`consumer_system_id`) REFERENCES `arrowhead_system` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `event_filter_metadata`
--

DROP TABLE IF EXISTS `event_filter_metadata`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `event_filter_metadata` (
  `filter_id` int(11) NOT NULL,
  `metadata_value` varchar(2047) DEFAULT NULL,
  `metadata_key` varchar(255) NOT NULL,
  PRIMARY KEY (`filter_id`,`metadata_key`),
  CONSTRAINT `FK1iu2vhxo8211io6weiwryguib` FOREIGN KEY (`filter_id`) REFERENCES `event_filter` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `event_filter_sources_list`
--

DROP TABLE IF EXISTS `event_filter_sources_list`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `event_filter_sources_list` (
  `filter_id` int(11) NOT NULL,
  `sources_id` int(11) NOT NULL,
  UNIQUE KEY `UK_nbe4wrcv5w6rga8uc6t0cb0ck` (`sources_id`),
  KEY `FKqihrii4ab12xo3oxp5d5pb77j` (`filter_id`),
  CONSTRAINT `FK7gulo44n997tr1146xxi2xhfe` FOREIGN KEY (`sources_id`) REFERENCES `arrowhead_system` (`id`),
  CONSTRAINT `FKqihrii4ab12xo3oxp5d5pb77j` FOREIGN KEY (`filter_id`) REFERENCES `event_filter` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hibernate_sequence`
--

DROP TABLE IF EXISTS `hibernate_sequence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hibernate_sequence` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hibernate_sequence`
--

LOCK TABLES `hibernate_sequence` WRITE;
/*!40000 ALTER TABLE `hibernate_sequence` DISABLE KEYS */;
INSERT INTO `hibernate_sequence` VALUES (25),(25);
/*!40000 ALTER TABLE `hibernate_sequence` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inter_cloud_authorization`
--

DROP TABLE IF EXISTS `inter_cloud_authorization`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `inter_cloud_authorization` (
  `id` int(11) NOT NULL,
  `consumer_cloud_id` int(11) DEFAULT NULL,
  `arrowhead_service_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKj4pymxepq7mf82wx7f8e4hd9b` (`consumer_cloud_id`,`arrowhead_service_id`),
  KEY `FKsh4gbm0vs76weoq1lti6awtwf` (`arrowhead_service_id`),
  CONSTRAINT `FKsh4gbm0vs76weoq1lti6awtwf` FOREIGN KEY (`arrowhead_service_id`) REFERENCES `arrowhead_service` (`id`),
  CONSTRAINT `FKsw50x8tjybx1jjrkj6aamxt8c` FOREIGN KEY (`consumer_cloud_id`) REFERENCES `arrowhead_cloud` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `intra_cloud_authorization`
--

DROP TABLE IF EXISTS `intra_cloud_authorization`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `intra_cloud_authorization` (
  `id` int(11) NOT NULL,
  `consumer_system_id` int(11) DEFAULT NULL,
  `provider_system_id` int(11) DEFAULT NULL,
  `arrowhead_service_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK4ie5ps7a6w40iqdte0u53mw1u` (`consumer_system_id`,`provider_system_id`,`arrowhead_service_id`),
  KEY `FKt01tq84ypy16yfpt2q9v7qn2b` (`provider_system_id`),
  KEY `FK1nx371ky16pl2rl0f4hk3puk4` (`arrowhead_service_id`),
  CONSTRAINT `FK1nx371ky16pl2rl0f4hk3puk4` FOREIGN KEY (`arrowhead_service_id`) REFERENCES `arrowhead_service` (`id`),
  CONSTRAINT `FK58r9imuaq3dy3o96w5xcxkemh` FOREIGN KEY (`consumer_system_id`) REFERENCES `arrowhead_system` (`id`),
  CONSTRAINT `FKt01tq84ypy16yfpt2q9v7qn2b` FOREIGN KEY (`provider_system_id`) REFERENCES `arrowhead_system` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `neighbor_cloud`
--

DROP TABLE IF EXISTS `neighbor_cloud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `neighbor_cloud` (
  `cloud_id` int(11) NOT NULL,
  PRIMARY KEY (`cloud_id`),
  CONSTRAINT `FK9j46xue240bjfr6u5vvi3qsmi` FOREIGN KEY (`cloud_id`) REFERENCES `arrowhead_cloud` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `neighbor_cloud`
--

LOCK TABLES `neighbor_cloud` WRITE;
/*!40000 ALTER TABLE `neighbor_cloud` DISABLE KEYS */;
INSERT INTO `neighbor_cloud` VALUES (2);
/*!40000 ALTER TABLE `neighbor_cloud` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orchestration_store`
--

DROP TABLE IF EXISTS `orchestration_store`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `orchestration_store` (
  `id` int(11) NOT NULL,
  `is_default` char(1) DEFAULT NULL,
  `instruction` varchar(255) DEFAULT NULL,
  `last_updated` datetime DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `priority` int(11) DEFAULT NULL,
  `consumer_system_id` int(11) DEFAULT NULL,
  `provider_cloud_id` int(11) DEFAULT NULL,
  `provider_system_id` int(11) DEFAULT NULL,
  `arrowhead_service_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK328vwkn9l8phjq4j276wb13w9` (`arrowhead_service_id`,`consumer_system_id`,`priority`,`is_default`),
  KEY `FKg9jtg1go2yety7s6qimnbqdtc` (`consumer_system_id`),
  KEY `FK4as8nlx9s4a6a9r6y4oswj5do` (`provider_cloud_id`),
  KEY `FK1a9yusgvqs0jrna2y8cgdeusb` (`provider_system_id`),
  CONSTRAINT `FK1a9yusgvqs0jrna2y8cgdeusb` FOREIGN KEY (`provider_system_id`) REFERENCES `arrowhead_system` (`id`),
  CONSTRAINT `FK4as8nlx9s4a6a9r6y4oswj5do` FOREIGN KEY (`provider_cloud_id`) REFERENCES `arrowhead_cloud` (`id`),
  CONSTRAINT `FKg9jtg1go2yety7s6qimnbqdtc` FOREIGN KEY (`consumer_system_id`) REFERENCES `arrowhead_system` (`id`),
  CONSTRAINT `FKnjr4mytp6bipwyc9sv9y1ip51` FOREIGN KEY (`arrowhead_service_id`) REFERENCES `arrowhead_service` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `orchestration_store_attributes`
--

DROP TABLE IF EXISTS `orchestration_store_attributes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `orchestration_store_attributes` (
  `store_entry_id` int(11) NOT NULL,
  `attribute_value` varchar(2047) DEFAULT NULL,
  `attribute_key` varchar(255) NOT NULL,
  PRIMARY KEY (`store_entry_id`,`attribute_key`),
  CONSTRAINT `FKrtqe93seoude4elrqmk1qdowj` FOREIGN KEY (`store_entry_id`) REFERENCES `orchestration_store` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `own_cloud`
--

DROP TABLE IF EXISTS `own_cloud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `own_cloud` (
  `cloud_id` int(11) NOT NULL,
  PRIMARY KEY (`cloud_id`),
  CONSTRAINT `FKr3avkpkrx88jt4atfmxewqkl8` FOREIGN KEY (`cloud_id`) REFERENCES `arrowhead_cloud` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `own_cloud`
--

LOCK TABLES `own_cloud` WRITE;
/*!40000 ALTER TABLE `own_cloud` DISABLE KEYS */;
INSERT INTO `own_cloud` VALUES (1);
/*!40000 ALTER TABLE `own_cloud` ENABLE KEYS */;
UNLOCK TABLES;


--
-- Table structure for table `service_registry`
--

DROP TABLE IF EXISTS `service_registry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `service_registry` (
  `id` int(11) NOT NULL,
  `end_of_validity` datetime DEFAULT NULL,
  `metadata` varchar(255) DEFAULT NULL,
  `port` int(11) DEFAULT NULL,
  `service_uri` varchar(255) DEFAULT NULL,
  `udp` char(1) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `arrowhead_service_id` bigint(20) DEFAULT NULL,
  `provider_system_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK3q3tqiu7f92u946p33plj5fxq` (`arrowhead_service_id`,`provider_system_id`),
  KEY `FK4lc944mp4x24pr09wuxbb08ky` (`provider_system_id`),
  CONSTRAINT `FK4lc944mp4x24pr09wuxbb08ky` FOREIGN KEY (`provider_system_id`) REFERENCES `arrowhead_system` (`id`),
  CONSTRAINT `FKr0x7pvbi16w5b6ao6q43t606p` FOREIGN KEY (`arrowhead_service_id`) REFERENCES `arrowhead_service` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-05-14 16:30:06
