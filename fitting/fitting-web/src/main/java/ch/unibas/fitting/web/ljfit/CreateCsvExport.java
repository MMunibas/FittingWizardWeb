package ch.unibas.fitting.web.ljfit;

import au.com.bytecode.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by mhelmer on 30.06.2016.
 */
public class CreateCsvExport {

    public File create(File exportDir, Input input) {
        File f = new File(exportDir, "density_and_DHVap.csv");
        CSVWriter csvw = null;
        try {
            csvw = new CSVWriter(new FileWriter(f));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            final String[] array1 = {"e_gas(kcal/mol)", "e_liquid(kcal/mol)", "temperature(K)", "molar_mass(g/mol)",
                    "density(g/cm^3)", "delta_H_vap(kcal/mol)", "delta_G_solv(kcal/mol)"};
            csvw.writeNext(array1);

            final String[] array2 = {
                    Double.toString(input.getEgas()),
                    Double.toString(input.getEliq()),
                    Double.toString(input.getTemp()),
                    Double.toString(input.getMmass()),
                    Double.toString(input.getDensity()),
                    Double.toString(input.getDeltaH()),
                    Double.toString(input.getDg())};
            csvw.writeNext(array2);

            csvw.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return f;
    }

    public static class Input{
        private double egas;
        private double eliq;
        private double temp;
        private double mmass;
        private double density;
        private double deltaH;
        private double dg;

        public double getEgas() {
            return egas;
        }

        public double getEliq() {
            return eliq;
        }

        public double getTemp() {
            return temp;
        }

        public double getMmass() {
            return mmass;
        }

        public double getDensity() {
            return density;
        }

        public double getDeltaH() {
            return deltaH;
        }

        public double getDg() {
            return dg;
        }

        public Input(double egas, double eliq, double temp, double mmass, double density, double deltaH, double dg) {
            this.egas = egas;
            this.eliq = eliq;
            this.temp = temp;
            this.mmass = mmass;
            this.density = density;
            this.deltaH = deltaH;
            this.dg = dg;
        }
    }
}
