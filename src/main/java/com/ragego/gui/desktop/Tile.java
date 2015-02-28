package com.ragego.gui.desktop;

import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;


/**
 * Created by brenault on 28/02/2015.
 */
public class Tile extends Parent{
    static String imgName = "goban_piece_middle.png";
    
    private int x, y; // Coordinates of the tile in the staggered system
    private double xPi, yPi; // Coordinates of the tile in pixels
    public ImageView content;
    
    public Tile(int ax, int ay , double axPi, double ayPi) throws IOException {
        x = ax;
        y = ay;
        xPi = axPi;
        yPi = ayPi;
        
        InputStream is = Files.newInputStream(Paths.get("src/main/resources/com/ragego/gui/desktop/" + imgName));
        Image tileSprite = new Image(is);
        content = new ImageView(tileSprite);
        
        content.setX(axPi);
        content.setY(ayPi);
    }
    
    public int getX (){
        return x;
    }
    public int getY (){
        return y;
    }
    public double getXPi (){
        return xPi;
    }
    public double getYPi (){
        return yPi;
    }
}
