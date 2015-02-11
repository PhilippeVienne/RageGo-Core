package com.ragego.gui.desktop;

import com.ragego.engine.GameBoard;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

/**
 * The most simple board in javafx.
 */
public class SimpleBoard extends GridPane {
    
    private GameBoard board;
    
    public SimpleBoard(){
        board = new GameBoard();
        setId("board");
        setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println(event.getX() + "," + event.getY());
            }
        });
        ImageView image = new ImageView(new Image(SimpleBoard.class.getResourceAsStream("go-ban.png")));
        image.setPreserveRatio(true);
        image.setSmooth(true);
        image.setCache(true);
        add(image,0,0);
//        for(int i=0;i<=GameBoard.DEFAULT_BOARD_SIZE;i++)
//            for(int j=0;j<=GameBoard.DEFAULT_BOARD_SIZE;j++)
//                add(new Button("E"),i,j);
    }
    
}
