package com.ragego.gui.desktop;

import javafx.scene.Parent;

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
    final int MAP_WIDTH = 3;
    final int MAP_HEIGHT = 8;
    final double dx = 220; // x-Delta between two imgViews corners
    final double dy = 63; // y-Delta between two imgViews corners
    
    Tile[][] index;

    /**
     * Initializes the tile table and adds children.
     */
    public ZIndex () throws IOException {
        index = new Tile[MAP_WIDTH][MAP_HEIGHT];
        for (int j = 0 ; j < MAP_HEIGHT ; j++) {
            for (int i = 0 ; i < MAP_WIDTH ; i++) {
                // Shifts the odd rows a few pixels to the right for coherence
                if (j % 2 != 0) {
                    index[i][j] = new Tile(i, j, i*dx+0.5*dx, j*dy);
                }
                else
                    index[i][j] = new Tile(i, j, i*dx, j*dy);
                getChildren().add(index[i][j].content);
            }
        }
    }
}
