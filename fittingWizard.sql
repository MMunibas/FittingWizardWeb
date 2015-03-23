-- phpMyAdmin SQL Dump
-- version 4.3.11.1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Mar 23, 2015 at 07:09 PM
-- Server version: 5.5.41-MariaDB
-- PHP Version: 5.5.22

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `fittingWizard`
--

-- --------------------------------------------------------

--
-- Table structure for table `compounds`
--

CREATE TABLE IF NOT EXISTS `compounds` (
  `id` int(11) NOT NULL,
  `name` varchar(1024) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `compounds`
--

INSERT INTO `compounds` (`id`, `name`) VALUES
(1, 'phenol'),
(1, 'phénol'),
(1, 'Benzenol'),
(1, 'CARBOLIC ACID'),
(1, 'Phenic Acid'),
(1, 'Phenylic Acid'),
(1, 'Phenylic alcohol'),
(1, 'Karbolsäure'),
(1, 'Karbol');

-- --------------------------------------------------------

--
-- Table structure for table `prop`
--

CREATE TABLE IF NOT EXISTS `prop` (
  `id` int(11) NOT NULL,
  `mass` double DEFAULT NULL,
  `density` double DEFAULT NULL,
  `Hvap` double DEFAULT NULL,
  `Gsolv` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `prop`
--

INSERT INTO `prop` (`id`, `mass`, `density`, `Hvap`, `Gsolv`) VALUES
(1, 94.11, 1.07, NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `structure`
--

CREATE TABLE IF NOT EXISTS `structure` (
  `id` int(11) NOT NULL,
  `formula` varchar(1024) NOT NULL,
  `inchi` varchar(1024) DEFAULT NULL,
  `smiles` varchar(1024) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `structure`
--

INSERT INTO `structure` (`id`, `formula`, `inchi`, `smiles`) VALUES
(1, 'C6H6O', 'InChI=1S/C6H6O/c7-6-4-2-1-3-5-6/h1-5,7H', 'Oc1ccccc1');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `compounds`
--
ALTER TABLE `compounds`
  ADD KEY `id` (`id`);

--
-- Indexes for table `prop`
--
ALTER TABLE `prop`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `structure`
--
ALTER TABLE `structure`
  ADD PRIMARY KEY (`id`);

--
-- Constraints for dumped tables
--

--
-- Constraints for table `prop`
--
ALTER TABLE `prop`
ADD CONSTRAINT `prop_ibfk_1` FOREIGN KEY (`id`) REFERENCES `compounds` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `structure`
--
ALTER TABLE `structure`
ADD CONSTRAINT `structure_ibfk_1` FOREIGN KEY (`id`) REFERENCES `compounds` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
