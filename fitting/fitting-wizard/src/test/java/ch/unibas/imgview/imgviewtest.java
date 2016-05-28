/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.GroupBuilder;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.ImageViewBuilder;
import javafx.stage.Stage;
 
public class imgviewtest extends Application {
 
    public static void main(String[] args) {
        launch(args);
    }
     
    @Override
    public void start(Stage primaryStage) {

        Group root = new Group();
                 
        String imageSource = "http://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/6343/PNG?image_size=400x300";
         
        ImageView imageView = new ImageView();
         
        Group myGroup = GroupBuilder.create()
                .children(imageView)
                .build();
         
        root.getChildren().add(myGroup);
   
        primaryStage.setScene(new Scene(root, 500, 400));
        primaryStage.show();
    }
}