/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.unibas.babelBinding;

/**
 *
 * @author hedin
 */
import org.openbabel.*;

public class BabelTest {

    public static void main(String[] args) {
        // Initialise
        System.loadLibrary("openbabel_java");

        // Read molecule from SMILES string
        OBConversion conv = new OBConversion();
        OBMol mol = new OBMol();
        conv.SetInFormat("smi");
        conv.ReadString(mol, "C(Cl)(=O)CCC(=O)Cl");

        // Print out some general information on the molecule, atoms
        conv.SetOutFormat("can");
        System.out.print("Canonical SMILES: " + conv.WriteString(mol));
        System.out.println("The molecular weight of the molecule is "
                + mol.GetMolWt());
        for (OBAtom atom : new OBMolAtomIter(mol)) {
            System.out.println("Atom " + atom.GetIdx()
                    + ": atomic number = " + atom.GetAtomicNum()
                    + ", hybridisation = " + atom.GetHyb());
        }

       // What are the indices of the carbon atoms
        // of the acid chloride groups?
        OBSmartsPattern acidpattern = new OBSmartsPattern();
        acidpattern.Init("C(=O)Cl");
        acidpattern.Match(mol);

        vectorvInt matches = acidpattern.GetUMapList();
        System.out.println("There are " + matches.size()
                + " acid chloride groups");
        System.out.print("The carbon atoms of the matches are: ");
        for (int i = 0; i < matches.size(); i++) {
            System.out.print(matches.get(i).get(0) + " ");
        }
    }
}
