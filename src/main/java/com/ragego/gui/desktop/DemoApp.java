package com.ragego.gui.desktop;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * JavaFX Application Demo.
 * Launch a simple demo of RageGo without any gameplay. 
 */
public class DemoApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        StackPane root = new StackPane();
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(DemoApp.class.getResource("style.css").toExternalForm());
        primaryStage.setScene(scene);
        BorderPane group = new BorderPane();
        group.setCenter(new SimpleBoard());
        Text player1 = new Text(0, 100, "Player 1");
        shadow(player1);
        Text player2 = new Text(0, 100, "Player 2");
        shadow(player2);
        group.setLeft(new BorderPane(player1));
        group.setRight(new BorderPane(player2));
        root.getChildren().add(group);
        root.setId("pane");
        primaryStage.show();
    }
    
    public void shadow(Text sample){
        InnerShadow innerShadow = new InnerShadow();
        sample.setFont(Font.font("Arial Black", 25));
        sample.setFill(Color.web("#FFF"));
        innerShadow.setRadius(5d);
        innerShadow.setOffsetX(2);
        innerShadow.setOffsetY(2);
        sample.setEffect(innerShadow);
        
    }
    
    public static void main(String ... args){
        launch(DemoApp.class,args);
    }
}
