/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.gui.database.dataModel;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author hedin
 */
/**
 * A class containing result of DB queries
 */
public class DB_model {

    private final StringProperty name = new SimpleStringProperty(this, "name");
    private final StringProperty formula = new SimpleStringProperty(this, "formula");
    private final StringProperty smiles = new SimpleStringProperty(this, "smiles");
    private final DoubleProperty mass = new SimpleDoubleProperty(this, "mass");
    private final DoubleProperty density = new SimpleDoubleProperty(this, "density");
    private final DoubleProperty dh = new SimpleDoubleProperty(this, "dh");
    private final DoubleProperty dg = new SimpleDoubleProperty(this, "dg");

    
    public DB_model(String _name, String _formula, String _smiles,
            double _mass, double _density, double _dh, double _dg) {
        this.setName(_name);
        this.setFormula(_formula);
        this.setSmiles(_smiles);
        this.setMass(_mass);
        this.setDensity(_density);
        this.setDh(_dh);
        this.setDg(_dg);
    }
        
    //------------------------------------
    public StringProperty nameProperty() {
        return name;
    }

    public final String getName() {
        return nameProperty().get();
    }

    public final void setName(String _name) {
        nameProperty().set(_name);
    }

    //------------------------------------
    public StringProperty formulaProperty() {
        return formula;
    }

    public final String getFormula() {
        return formulaProperty().get();
    }

    public final void setFormula(String _formula) {
        formulaProperty().set(_formula);
    }

    //------------------------------------
    public StringProperty smilesProperty() {
        return smiles;
    }

    public final String getSmiles() {
        return smilesProperty().get();
    }

    public final void setSmiles(String _smiles) {
        smilesProperty().set(_smiles);
    }

    //------------------------------------
    public DoubleProperty massProperty() {
        return mass;
    }

    public final double getMass() {
        return massProperty().get();
    }

    public final void setMass(double _mass) {
        massProperty().set(_mass);
    }

    //------------------------------------
    public DoubleProperty densityProperty() {
        return density;
    }

    public final double getDensity() {
        return densityProperty().get();
    }

    public final void setDensity(double _density) {
        densityProperty().set(_density);
    }

    //------------------------------------
    public DoubleProperty dhProperty() {
        return dh;
    }

    public final double getDh() {
        return dhProperty().get();
    }

    public final void setDh(double _dh) {
        dhProperty().set(_dh);
    }
    
    //------------------------------------
    public DoubleProperty dgProperty() {
        return dg;
    }

    public final double getDg() {
        return dgProperty().get();
    }

    public final void setDg(double _dg) {
        dgProperty().set(_dg);
    }    

    @Override
    public String toString() {
        String ret="";
        
        ret += "name : " + name.get() + '\n';
        ret += "formula : " + formula.get() + '\n';
        ret += "smiles : " + smiles.get() + '\n';
        ret += "mass : " + mass.get() + '\n';
        ret += "density : " + density.get() + '\n';
        ret += "dh : " + dh.get() + '\n';
        ret += "dg : " + dg.get() + '\n';
        
        return ret;
    }
    
    
        
}
