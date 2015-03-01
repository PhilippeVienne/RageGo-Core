package com.ragego.gui.desktop;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * JavaFX Application GUI
 * Manages everything related to the user interface.
 * @author brenault
 */
public class RageGoGUI extends Application{
    /**
     * Generates the initial state of the game 
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setFullScreen(true);
        
        double screenWidth = Screen.getPrimary().getBounds().getWidth();
        double screenHeight = Screen.getPrimary().getBounds().getHeight();
        
        Pane root = new Pane();
        root.setPrefSize(screenWidth, screenHeight);
        root.setId("borderpane");
        
        /* Use if decides to make a three-panel interface
        Pane controlsLeft = new Pane();
        controlsLeft.setPrefSize(0.5*(screenWidth-screenHeight), screenHeight);
        
        Pane display = new Pane();
        display.setPrefSize(screenHeight, screenHeight);
        
        Pane controlsRight = new Pane();
        controlsRight.setPrefSize(0.5*(screenWidth-screenHeight), screenHeight);

        root.setLeft(controlsLeft);
        root.setCenter(display);
        root.setRight(controlsRight);
        */
        
        Scene skirmishScene = new Scene(root, Color.BLACK);
        skirmishScene.getStylesheets().addAll(this.getClass().getResource("GUIStyle.css").toExternalForm());
        
        //Adding the map
        ZIndex testIndex = new ZIndex(screenWidth);

        root.getChildren().add(testIndex.getZIndexLayer());
        
        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(skirmishScene);
        primaryStage.show();
    }
    
    public static void main (String[] args) {
        launch(args);
    }
}
