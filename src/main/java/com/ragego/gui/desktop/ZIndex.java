package com.ragego.gui.desktop;

import javafx.scene.Parent;
import javafx.scene.layout.Pane;

import java.io.IOException;

/**
 * Created by brenault on 28/02/2015.
 */

/**
 * Defines the ZIndex object, which may be considered a layer
 * Is just the equivalent of a ZIndex for now
 * @author brenault 
 */
public class ZIndex extends Parent{
    final int MAP_WIDTH = 20;
    final int MAP_HEIGHT = 20;
    final double IMAGE_WIDTH = 250;
    double coeff; // Resizing coefficient
    double dx = 220; // x-Delta between two imgViews corners
    double dy = 63; // y-Delta between two imgViews corners
    
    Tile[][] index;
    Pane layer;

    /**
     * Initializes the tile table and adds children.
     */
    public ZIndex (double displayWidth) throws IOException {
        index = new Tile[MAP_WIDTH][MAP_HEIGHT];
        layer = new Pane();

        double theoWidth = (MAP_WIDTH - 1) * IMAGE_WIDTH + 1.5 * dx;
        System.out.println("theoWidth : "+theoWidth);
        System.out.println("displayWidth : "+displayWidth);
        coeff = displayWidth/theoWidth;
        System.out.println("coeff : "+coeff);
        
        for (int j = 0 ; j < MAP_HEIGHT ; j++) {
            for (int i = 0 ; i < MAP_WIDTH ; i++) {
                // Shifts the odd rows a few pixels to the right for coherence
                if (j % 2 != 0) {
                    index[i][j] = new Tile(i, j, (i*dx+0.5*dx), (j*dy), coeff);
                }
                else
                    index[i][j] = new Tile(i, j, (i*dx), (j*dy), coeff);

                layer.getChildren().add(index[i][j].getTileContent());
            }
        }
    }

    public Pane getZIndexLayer (){
        return layer;
    }
}
