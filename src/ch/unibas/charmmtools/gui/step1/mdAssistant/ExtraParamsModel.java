/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.gui.step1.mdAssistant;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author hedin
 */
public class ExtraParamsModel {
    
    private StringProperty parameter = null;
    private StringProperty value = null;
    
    public ExtraParamsModel(String parameter, String value) {
        this.parameter = new SimpleStringProperty(parameter);
        this.value = new SimpleStringProperty(value);
    }
    
    public StringProperty parameterProperty() {
        return parameter;
    }

    public String getParameter() {
        return this.parameter.get();
    }

    public void setParameter(String par) {
        this.parameter.set(par);
    }
    
    public StringProperty valueProperty() {
        return value;
    }

    public String getValue() {
        return this.value.get();
    }

    public void setValue(String val) {
        this.value.set(val);
    }
        
}
