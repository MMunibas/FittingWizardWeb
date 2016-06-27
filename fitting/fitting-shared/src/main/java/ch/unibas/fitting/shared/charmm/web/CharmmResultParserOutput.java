package ch.unibas.fitting.shared.charmm.web;

/**
 * Created by tschmidt on 27.06.2016.
 */
public class CharmmResultParserOutput {
    private double temp;
    private int nres;
    private int natom;
    private int nconstr;

    private double box;

    private double egas;
    private double eliq;

    private double gas_mtp;
    private double gas_vdw;
    private double solvent_mtp;
    private double solvent_vdw;

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public int getNres() {
        return nres;
    }

    public void setNres(int nres) {
        this.nres = nres;
    }


    public double getGas_mtp() {
        return gas_mtp;
    }

    public void setGas_mtp(double gas_mtp) {
        this.gas_mtp = gas_mtp;
    }

    public double getGas_vdw() {
        return gas_vdw;
    }

    public void setGas_vdw(double gas_vdw) {
        this.gas_vdw = gas_vdw;
    }

    public double getSolvent_mtp() {
        return solvent_mtp;
    }

    public void setSolvent_mtp(double solvent_mtp) {
        this.solvent_mtp = solvent_mtp;
    }

    public double getSolvent_vdw() {
        return solvent_vdw;
    }

    public void setSolvent_vdw(double solvent_vdw) {
        this.solvent_vdw = solvent_vdw;
    }

    public double getBox() {
        return box;
    }

    public void setBox(double box) {
        this.box = box;
    }

    public int getNatom() {
        return natom;
    }

    public void setNatom(int natom) {
        this.natom = natom;
    }

    public int getNconstr() {
        return nconstr;
    }

    public void setNconstr(int nconstr) {
        this.nconstr = nconstr;
    }

    public double getEgas() {
        return egas;
    }

    public void setEgas(double egas) {
        this.egas = egas;
    }

    public double getEliq() {
        return eliq;
    }

    public void setEliq(double eliq) {
        this.eliq = eliq;
    }
}
