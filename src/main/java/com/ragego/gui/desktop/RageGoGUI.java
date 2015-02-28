package com.ragego.gui.desktop;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Created by brenault on 28/02/2015.
 */
public class RageGoGUI extends Application{
    @Override
    public void start(Stage primaryStage) throws Exception {
        Pane root = new Pane();
        
        Scene mainScene = new Scene(root, 1200, 650, Color.BLACK);
        
        Map testMap = new Map();
        root.getChildren().add(testMap);
        
        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }
    
    public static void main (String[] args) {
        launch(args);
    }
}
