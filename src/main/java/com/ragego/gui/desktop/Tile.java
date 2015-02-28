package com.ragego.gui.desktop;

import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;


/**
 * Defines the Tile object, which is the basic element of the map
 * @author brenault
 */
public class Tile extends Parent{
    static String imgName = "goban_piece_middle.png";
    
    private int x, y; // Coordinates of the tile in the staggered system
    private double xPi, yPi; // Coordinates of the tile in pixels
    public ImageView content; // ImageView of the tile

    /**
     * Initializes attributes, fetches sprite and affects it to the right spot
     * @param ax x-coordinate
     * @param ay y-coordinate
     * @param axPi x-coordinate in pixels
     * @param ayPi y-coordinate in pixels
     */
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

    /**
     * x getter
     */
    public int getX (){
        return x;
    }
    
    /**
     * y getter
     */
    public int getY (){
        return y;
    }
    /**
     * xPi getter
     */
    public double getXPi (){
        return xPi;
    }
    /**
     * yPi getter
     */
    public double getYPi (){
        return yPi;
    }
}
