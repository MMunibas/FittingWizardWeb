/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.gaussian.base.dialog;

import ch.unibas.fittingwizard.gaussian.base.ui.MainWindow;
import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.control.LabelBuilder;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.apache.log4j.Logger;

/**
 * User: mhelmer Date: 25.11.13 Time: 17:58
 */
public class OverlayDialog extends Stage {
    
    protected final Logger logger;
    private Window primary;
    private Boolean result;

    private OverlayDialog(Scene parent, String title, String message, String trueText, String falseText) {
        super(StageStyle.TRANSPARENT);
        
        logger = Logger.getLogger(getClass());
        
        initModality(Modality.WINDOW_MODAL);
        if (parent != null) {
            assert getScene() != null : "Parent has no scene. Maybe the parent has not been added to a scene yet?";
            primary = parent.getWindow();
            initOwner(primary);
        }

        if (message == null || message.isEmpty()) {
            message = title;
            title = null;
        }

        ArrayList<Node> verticalNodes = new ArrayList<>();
        if (title != null && !title.isEmpty()) {
            verticalNodes.add(LabelBuilder.create().text(title).build());
        }

        ArrayList<Node> horizontalNodes = new ArrayList<>();
        horizontalNodes.add(LabelBuilder.create().text(message).build());
        if (trueText != null && !trueText.isEmpty()) {
            horizontalNodes.add(ButtonBuilder.create().text(trueText).onAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    result = true;
                    hideBlurryEffect();
                    close();
                }
            }).build());
        }

        if (falseText != null && !falseText.isEmpty()) {
            horizontalNodes.add(ButtonBuilder.create().text(falseText).defaultButton(true).onAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    result = false;
                    hideBlurryEffect();
                    close();
                }
            }).build());
        }

        verticalNodes.add(HBoxBuilder.create().children(horizontalNodes).build());

        Scene root = new Scene(
                VBoxBuilder.create().styleClass("modal-dialog").children(verticalNodes).build(), Color.TRANSPARENT
        );
        setScene(root);
        if (parent != null) {
            getScene().getStylesheets().addAll(parent.getStylesheets());
        }

        final Delta dragDelta = new Delta();
        root.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                // record a delta distance for the drag and drop operation.
                dragDelta.x = getX() - mouseEvent.getScreenX();
                dragDelta.y = getY() - mouseEvent.getScreenY();
            }
        });
        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                setX(mouseEvent.getScreenX() + dragDelta.x);
                setY(mouseEvent.getScreenY() + dragDelta.y);
            }
        });

        // TODO fix this
        //centerOnParent(primary);
    }

    private OverlayDialog(Scene parent, String title, String message, String okText, String dirText, String dirPath) {
        super(StageStyle.TRANSPARENT);

        logger = Logger.getLogger(getClass());
        
        initModality(Modality.WINDOW_MODAL);
        if (parent != null) {
            assert getScene() != null : "Parent has no scene. Maybe the parent has not been added to a scene yet?";
            primary = parent.getWindow();
            initOwner(primary);
        }

        if (message == null || message.isEmpty()) {
            message = title;
            title = null;
        }

        ArrayList<Node> verticalNodes = new ArrayList<>();
        if (title != null && !title.isEmpty()) {
            verticalNodes.add(LabelBuilder.create().text(title).build());
        }

        ArrayList<Node> horizontalNodes = new ArrayList<>();
        horizontalNodes.add(LabelBuilder.create().text(message).build());
        if (okText != null && !okText.isEmpty()) {
            horizontalNodes.add(ButtonBuilder.create().text(okText).onAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    result = true;
                    hideBlurryEffect();
                    close();
                }
            }).build());
        }

        if (dirText != null && !dirText.isEmpty()) {
            horizontalNodes.add(ButtonBuilder.create().text(dirText).defaultButton(true).onAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    result = true;

                    try {
                        if (Desktop.isDesktopSupported()) {
//                            Desktop.getDesktop().open(new File(dirPath));
//                            Desktop.getDesktop().browse(new File(dirPath).toURI());
                              Desktop.getDesktop().open(new File(new URI("file:///home/hedin").getPath()));// this launches Dolphin
                        }
                    } catch (Exception e) {
                        logger.error("Exception caught while opening directory viewer : " + e.getMessage());
                    }

                }
            }).build());
        }

        verticalNodes.add(HBoxBuilder.create().children(horizontalNodes).build());

        Scene root = new Scene(
                VBoxBuilder.create().styleClass("modal-dialog").children(verticalNodes).build(), Color.TRANSPARENT
        );
        setScene(root);
        if (parent != null) {
            getScene().getStylesheets().addAll(parent.getStylesheets());
        }

        final Delta dragDelta = new Delta();
        root.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                // record a delta distance for the drag and drop operation.
                dragDelta.x = getX() - mouseEvent.getScreenX();
                dragDelta.y = getY() - mouseEvent.getScreenY();
            }
        });
        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                setX(mouseEvent.getScreenX() + dragDelta.x);
                setY(mouseEvent.getScreenY() + dragDelta.y);
            }
        });

        // TODO fix this
        //centerOnParent(primary);
    }

    private void centerOnParent(Window primary) {
        sizeToScene();
        double x = primary.getX() + primary.getWidth() / 2 - getWidth() / 2;
        double y = primary.getY() + primary.getHeight() / 2 - getHeight() / 2;

        setX(x);
        setY(y);
    }

    private void showBlurryEffectOnParent() {
        if (primary != null) {
            primary.getScene().getRoot().setEffect(new BoxBlur());
        }
    }

    private void hideBlurryEffect() {
        if (primary != null) {
            primary.getScene().getRoot().setEffect(null);
        }
    }

    private Boolean showAndWaitForResult() {
        showBlurryEffectOnParent();
        result = null;
        super.showAndWait();
        return result;
    }

    public static boolean askYesOrNo(String title) {
        return internalShowOverlay(null, title, null, "Yes", "No");
    }

    public static boolean askOkOrCancel(String message) {
        return internalShowOverlay(null, message, null, "Ok", "Cancel");
    }

    public static void showError(String title, String message) {
        internalShowOverlay(null, title, message, "Ok", null);
    }

    public static void informUser(String title, String message) {
        internalShowOverlay(null, title, message, "Ok", null);
    }

    public static void informUserDir(String title, String message, String directory) {
        /* TODO : 
        Check why directory opening has a bug
        */
//        internalShowOverlay(null, title, message, "Ok", "Open directory", directory);
        internalShowOverlay(null, title, message, "Ok", null);
    }

    private static boolean internalShowOverlay(Node parent, String title, String message, String trueText, String falseText) {
        Scene scene = null;
        if (parent == null) {
            Stage primaryStage = MainWindow.getPrimaryStage();
            if (primaryStage != null) {
                scene = primaryStage.getScene();
            }
        } else {
            scene = parent.getScene();
        }
        OverlayDialog dialog = new OverlayDialog(scene, title, message, trueText, falseText);
        dialog.showAndWaitForResult();
        return dialog.result;
    }

    private static boolean internalShowOverlay(Node parent, String title, String message, String okText, String dirText, String dirPath) {
        Scene scene = null;
        if (parent == null) {
            Stage primaryStage = MainWindow.getPrimaryStage();
            if (primaryStage != null) {
                scene = primaryStage.getScene();
            }
        } else {
            scene = parent.getScene();
        }
        OverlayDialog dialog = new OverlayDialog(scene, title, message, okText, dirText, dirPath);
        dialog.showAndWaitForResult();
        return dialog.result;
    }

    // records relative x and y co-ordinates.
    private class Delta {

        double x, y;
    }
}
