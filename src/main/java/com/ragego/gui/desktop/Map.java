package com.ragego.gui.desktop;

import javafx.scene.Parent;

import java.io.IOException;

/**
 * Created by brenault on 28/02/2015.
 */
public class Map extends Parent{
    final int MAP_WIDTH = 5;
    final int MAP_HEIGHT = 8;
    final double dx = 220; // x-Delta between two imgViews corners
    final double dy = 63; // y-Delta between two imgViews corners
    
    Tile[][] map;
    
    public Map () throws IOException {
        map = new Tile[MAP_WIDTH][MAP_HEIGHT];
        for (int j = 0 ; j < MAP_HEIGHT ; j++) {
            for (int i = 0 ; i < MAP_WIDTH ; i++) {
                if (j % 2 != 0) {
                    map[i][j] = new Tile(i, j, i*dx+0.5*dx, j*dy);
                }
                else
                    map[i][j] = new Tile(i, j, i*dx, j*dy);
                getChildren().add(map[i][j].content);
            }
        }
    }
}
