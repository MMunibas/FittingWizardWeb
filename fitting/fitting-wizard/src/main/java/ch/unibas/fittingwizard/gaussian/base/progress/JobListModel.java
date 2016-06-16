/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.gaussian.base.progress;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
/**
 * Structure for keeping data from list of jobs coming from qstat
 * 
 * @author hedin
 */
public class JobListModel {
    
    /*
     * Fields
     */

    private IntegerProperty jobID = null;
    private StringProperty jobName = null;
    private StringProperty user = null;
    private StringProperty state = null;
    private StringProperty date = null;
    private IntegerProperty ncpu = null;
    
    public JobListModel(int id, String name, String user, String state, String date, int ncpu)
    {
        this.jobID = new SimpleIntegerProperty(id);
        this.jobName = new SimpleStringProperty(name);
        this.user = new SimpleStringProperty(user);
        this.state = new SimpleStringProperty(state);
        this.date = new SimpleStringProperty(date);
        this.ncpu = new SimpleIntegerProperty(ncpu);
    }

    /**
     * @return the jobID
     */
    public int getJobID() {
        return jobID.get();
    }

    /**
     * @param jobID the jobID to set
     */
    public void setJobID(int jobID) {
        this.jobID.set(jobID);
    }

    /**
     * @return the jobName
     */
    public String getJobName() {
        return jobName.get();
    }

    /**
     * @param jobName the jobName to set
     */
    public void setJobName(String jobName) {
        this.jobName.set(jobName);
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user.get();
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user.set(user);
    }

    /**
     * @return the state
     */
    public String getState() {
        return state.get();
    }

    /**
     * @param state the state to set
     */
    public void setState(String state) {
        this.state.set(state);
    }

    /**
     * @return the date
     */
    public String getDate() {
        return date.get();
    }

    /**
     * @param date the date to set
     */
    public void setDate(String date) {
        this.date.set(date);
    }

    /**
     * @return the ncpu
     */
    public int getNcpu() {
        return ncpu.get();
    }

    /**
     * @param ncpu the ncpu to set
     */
    public void setNcpu(int ncpu) {
        this.ncpu.set(ncpu);
    }
    
    
    
}
