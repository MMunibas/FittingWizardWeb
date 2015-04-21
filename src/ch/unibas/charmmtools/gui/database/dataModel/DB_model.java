/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.gui.database.dataModel;

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
    private final StringProperty mass = new SimpleStringProperty(this, "mass");
    private final StringProperty density = new SimpleStringProperty(this, "density");
    private final StringProperty dh = new SimpleStringProperty(this, "dh");
    private final StringProperty dg = new SimpleStringProperty(this, "dg");

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
    public StringProperty massProperty() {
        return mass;
    }

    public final String getMass() {
        return massProperty().get();
    }

    public final void setMass(String _mass) {
        massProperty().set(_mass);
    }

    //------------------------------------
    public StringProperty densityProperty() {
        return density;
    }

    public final String getDensity() {
        return densityProperty().get();
    }

    public final void setDensity(String _density) {
        densityProperty().set(_density);
    }

    //------------------------------------
    public StringProperty dhProperty() {
        return dh;
    }

    public final String getDh() {
        return dhProperty().get();
    }

    public final void setDh(String _dh) {
        dhProperty().set(_dh);
    }
    
    //------------------------------------
    public StringProperty dgProperty() {
        return dg;
    }

    public final String getDg() {
        return dgProperty().get();
    }

    public final void setDg(String _dg) {
        dgProperty().set(_dg);
    }    

    public DB_model(String _name, String _formula, String _smiles,
            String _mass, String _density, String _dh, String _dg) {
        this.setName(_name);
        this.setFormula(_formula);
        this.setSmiles(_smiles);
        this.setMass(_mass);
        this.setDensity(_density);
        this.setDh(_dh);
        this.setDg(_dg);
    }
    
    
        
}
