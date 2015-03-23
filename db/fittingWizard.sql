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
);

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
  `Gsolv` double DEFAULT NULL,
  FOREIGN KEY(id) REFERENCES compounds(id)
);

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
  `smiles` varchar(1024) DEFAULT NULL,
  FOREIGN KEY(id) REFERENCES compounds(id)
);

--
-- Dumping data for table `structure`
--

INSERT INTO `structure` (`id`, `formula`, `inchi`, `smiles`) VALUES
(1, 'C6H6O', 'InChI=1S/C6H6O/c7-6-4-2-1-3-5-6/h1-5,7H', 'Oc1ccccc1');

