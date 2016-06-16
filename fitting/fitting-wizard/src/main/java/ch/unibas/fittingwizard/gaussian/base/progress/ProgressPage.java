/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.gaussian.base.progress;

import ch.unibas.fittingwizard.gaussian.base.ButtonFactory;
import ch.unibas.fittingwizard.gaussian.base.CatchedRunnable;
import ch.unibas.fittingwizard.gaussian.base.WizardPage;
import ch.unibas.fittingwizard.gaussian.base.dialog.OverlayDialog;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.apache.commons.lang.exception.ExceptionUtils;

public abstract class ProgressPage extends WizardPage implements Context {
    private Task<Boolean> task;

    @FXML
    protected Label lblTitle;

    public ProgressPage(String title) {
        super(title);
        removeButtonFromButtonBar(button_initialSelection);
    }

    protected abstract boolean run(final Context ctx) throws Exception;

    @Override
    public void setTitle(final String title) {
        logger.info(title);
        Platform.runLater(new CatchedRunnable() {
            @Override
            public void safelyRun() {
                lblTitle.textProperty().setValue(title);
            }
        });
    }

    @Override
    public void initializeData() {
        if (task == null) {
            task = new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    return ProgressPage.this.run(ProgressPage.this);
                }
            };
            task.setOnCancelled(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    logger.info("Task canceled.");
                    goBack();
                }
            });
            task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    logger.info("Task succeeded.");
                    final boolean result = task.getValue();
                    Platform.runLater(new CatchedRunnable() {
                        @Override
                        public void safelyRun() {
                            handleFinishedRun(result);
                        }
                    });
                    task = null;
                }
            });
            task.setOnFailed(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    logger.info("Task failed.");
                    String error = "No exception details available.";
                    if (task.getException() != null) {
                        error = ExceptionUtils.getMessage(task.getException());
                        logger.error("Task failed with exception.", task.getException());
                    }

                    OverlayDialog.showError("Error in the task execution",
                            "There was an error in the task execution.\n\n" + error);
                    goBack();
                }
            });
        }

        logger.info("Starting task");
        new Thread(task).start();
        logger.info("Task started.");
    }

    private void goBack() {
        task = null;
        handleCanceled();
    }

    protected abstract void handleCanceled();
    protected abstract void handleFinishedRun(boolean successful);

    @Override
    protected void fillButtonBar() {
        Button cancelButton = ButtonFactory.createButtonBarButton("Cancel", new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                logger.info("Canceling task");
                if (task != null)
                if (!task.isCancelled())
                    task.cancel();
                else
                    goBack();
            }
        });
        addButtonToButtonBar(cancelButton);
    }

    @Override
    protected Class getTypeForFxml() {
        return ProgressPage.class;
    }
}
