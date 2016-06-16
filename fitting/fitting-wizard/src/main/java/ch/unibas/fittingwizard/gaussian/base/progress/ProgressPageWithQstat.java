/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.gaussian.base.progress;

import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 *
 * @author hedin
 */
public abstract class ProgressPageWithQstat extends ProgressPage{
    
    @FXML // fx:id="TableJobs"
    private TableView<JobListModel> TableJobs; // Value injected by FXMLLoader
    
    @FXML // fx:id="colName"
    private TableColumn<JobListModel, String> colName; // Value injected by FXMLLoader

    @FXML // fx:id="colUser"
    private TableColumn<JobListModel, String> colUser; // Value injected by FXMLLoader

    @FXML // fx:id="colStart"
    private TableColumn<JobListModel, String> colDate; // Value injected by FXMLLoader

    @FXML // fx:id="colState"
    private TableColumn<JobListModel, String> colState; // Value injected by FXMLLoader

    @FXML // fx:id="colID"
    private TableColumn<JobListModel, Integer> colID; // Value injected by FXMLLoader

    @FXML // fx:id="colCPUs"
    private TableColumn<JobListModel, Integer> colCPUs; // Value injected by FXMLLoader

    public ProgressPageWithQstat(String title) {
        super(title);
        initTable();
    }

    private void initTable() {
        
        colID.setCellValueFactory(
                new PropertyValueFactory<>("jobID")
        );
        
        colName.setCellValueFactory(
                new PropertyValueFactory<>("jobName")
        );
        
        colUser.setCellValueFactory(
                new PropertyValueFactory<>("user")
        );
        
        colState.setCellValueFactory(
                new PropertyValueFactory<>("state")
        );
        
        colDate.setCellValueFactory(
                new PropertyValueFactory<>("date")
        );
        
        colCPUs.setCellValueFactory(
                new PropertyValueFactory<>("ncpu")
        );
        
    }
    
    protected void fillTable()
    {
        
        List<JobListModel> jobs = new ArrayList<>();
        
        //377374 10.60000 deoxy120   hedin        r     11/30/2015 14:55:00 gpu.q@studix10.chemie.unibas.c     6
        jobs.add(new JobListModel(377374, "testjob", "hedin", "r", "11/30/2015 14:55:00",1));
        
        TableJobs.getItems().clear();
        TableJobs.getItems().addAll(jobs);
    }
    
    @Override
    protected Class getTypeForFxml() {
        return ProgressPageWithQstat.class;
    }
    
    
}
