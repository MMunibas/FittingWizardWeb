/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.gui.step4.ParGrid;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * The data model used when selecting the atoms to scale
 *
 * @author hedin
 */
public class SelectScalingModel {
    /*
     * Fields
     */

    private StringProperty FFatomType = null;
    private StringProperty MTPatomType = null;
    private BooleanProperty isSelected = null;


    /* 
     * Constructors
     */
    public SelectScalingModel(String FFatomType, boolean isSelected) {
        this.FFatomType = new SimpleStringProperty(FFatomType);
        this.isSelected = new SimpleBooleanProperty(isSelected);
    }
    
    public SelectScalingModel(String FFatomType, String MTPatomType, boolean isSelected) {
        this.FFatomType = new SimpleStringProperty(FFatomType);
        this.MTPatomType = new SimpleStringProperty(MTPatomType);
        this.isSelected = new SimpleBooleanProperty(isSelected);
    }

    public SelectScalingModel() {
        this(null, null, false);
    }

    /*
     * Properties
     */
    public StringProperty FFatomTypeProperty() {
        return FFatomType;
    }

    public String getFFAtomType() {
        return this.FFatomType.get();
    }

    public void setFFAtomType(String value) {
        this.FFatomType.set(value);
    }
    
    public StringProperty MTPatomTypeProperty() {
        return MTPatomType;
    }

    public String getMTPAtomType() {
        return this.MTPatomType.get();
    }

    public void setMTPAtomType(String value) {
        this.MTPatomType.set(value);
    }

    public BooleanProperty isSelectedProperty() {
        return isSelected;
    }

    public boolean isSelected() {
        return this.isSelected.get();
    }

    public void setisSelected(boolean value) {
        this.isSelected.set(value);
    }
}
