package com.ragego.gui.objects;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;

import java.awt.*;

public class Goban {
    private TiledMapTileLayer gridLayer;
    private Vector2 gobanOriginCoords;
    private int gobanSize;

    public Goban(TiledMapTileLayer gridLayer) {
        this.gridLayer = gridLayer;

        for (int x = 0; x < gridLayer.getWidth(); x++) {
            for (int y = 0; y < gridLayer.getHeight(); y++) {
                if (gridLayer.getCell(x, y).getTile().getProperties().get("name").equals("interTop")){
                    gobanOriginCoords = new Vector2(x, y);
                }
            }
        }
    }

    public Point isoToGoban (Point isoCoords){
        //If isoCoords.x too small or too big, or if isoCoords.y too small or too big
        return null;
    }
}
