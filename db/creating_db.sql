--
-- Database: `fittingWizard`
--

-- --------------------------------------------------------

-- Uncomment when importing in sqlite
PRAGMA foreign_keys=ON;

--
-- Table structure for table `compounds`
--

CREATE TABLE IF NOT EXISTS `compounds` (
  `id` integer PRIMARY KEY NOT NULL,
  `name` text
);

-- --------------------------------------------------------

--
-- Table structure for table `prop`
--

CREATE TABLE IF NOT EXISTS `prop` (
  `id` integer NOT NULL,
  `mass` double DEFAULT NULL,
  `density` double DEFAULT NULL,
  `Hvap` double DEFAULT NULL,
  `Gsolv` double DEFAULT NULL,
  FOREIGN KEY(id) REFERENCES compounds(id)
);

-- --------------------------------------------------------

--
-- Table structure for table `structure`
--

CREATE TABLE IF NOT EXISTS `structure` (
  `id` integer NOT NULL,
  `formula` text DEFAULT NULL,
  `inchi` text DEFAULT NULL,
  `smiles` text DEFAULT NULL,
  FOREIGN KEY(id) REFERENCES compounds(id)
);


