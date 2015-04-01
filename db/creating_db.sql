--
-- Database: `fittingWizard`
--

-- --------------------------------------------------------

-- Uncomment when importing in sqlite
PRAGMA foreign_keys=ON;

--
-- Table structure for table `compounds`
--

CREATE TABLE IF NOT EXISTS "compounds" (
  "id" INTEGER PRIMARY KEY NOT NULL,
  "idPubchem" INTEGER NOT NULL,
  "name" TEXT,
  "added" DATE NOT NULL
);

-- --------------------------------------------------------

--
-- Table structure for table `ff`
--

CREATE TABLE IF NOT EXISTS "ff" (
  "molID" INTEGER NOT NULL,
  "parID" INTEGER DEFAULT NULL,
  "topID" INTEGER DEFAULT NULL,
  FOREIGN KEY(molID) REFERENCES compounds(id),
  FOREIGN KEY(parID) REFERENCES par(id),
  FOREIGN KEY(topID) REFERENCES top(id)
);

-- --------------------------------------------------------

--
-- Table structure for table `par`
--

CREATE TABLE IF NOT EXISTS "par" (
  "id" INTEGER PRIMARY KEY NOT NULL,
  "parFile" BLOB NOT NULL,
  "description" TEXT NOT NULL
);

-- --------------------------------------------------------

--
-- Table structure for table `prop`
--

CREATE TABLE IF NOT EXISTS "prop" (
  "id" INTEGER NOT NULL,
  "mass" DOUBLE DEFAULT NULL,
  "density" DOUBLE DEFAULT NULL,
  "Hvap" DOUBLE DEFAULT NULL,
  "Gsolv" DOUBLE DEFAULT NULL,
  FOREIGN KEY(id) REFERENCES compounds(id)
);

-- --------------------------------------------------------

--
-- Table structure for table `ref`
--

CREATE TABLE IF NOT EXISTS "ref" (
  "id" INTEGER NOT NULL,
  "ref" TEXT,
  FOREIGN KEY(id) REFERENCES compounds(id)
);

-- --------------------------------------------------------

--
-- Table structure for table `structure`
--

CREATE TABLE IF NOT EXISTS "structure" (
  "id" INTEGER NOT NULL,
  "formula" TEXT,
  "inchi" TEXT,
  "smiles" TEXT,
  FOREIGN KEY(id) REFERENCES compounds(id)
);

-- --------------------------------------------------------

--
-- Table structure for table `top`
--

CREATE TABLE IF NOT EXISTS "top" (
  "id" INTEGER PRIMARY KEY NOT NULL,
  "topFile" BLOB NOT NULL,
  "description" TEXT NOT NULL
);


