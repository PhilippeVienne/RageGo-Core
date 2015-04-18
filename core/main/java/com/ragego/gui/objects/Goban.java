package com.ragego.gui.objects;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.ragego.engine.GameBoard;
import com.ragego.engine.HumanPlayer;
import com.ragego.gui.screens.GoGameScreen;

import java.awt.*;

public class Goban {
    private final GoGameScreen screen;
    private final TiledMap map;
    private final TiledMapTileLayer gridLayer;
    private final Vector2 gobanOriginCoords = new Vector2(0, 0);
    private final int gobanSize;
    private final GameBoard board;

    public Goban(GoGameScreen screen, TiledMap map, HumanPlayer humanPlayer, HumanPlayer player) {
        this.gridLayer = (TiledMapTileLayer) map.getLayers().get("grid");
        this.screen = screen;
        this.map = screen.getMap();
        for (int x = 0; x < gridLayer.getWidth(); x++) {
            for (int y = 0; y < gridLayer.getHeight(); y++) {
                if (gridLayer.getCell(x, y).getTile().getProperties().get("name").equals("interTop")){
                    gobanOriginCoords.set(x, y);
                }
            }
        }
        this.board = new GameBoard(humanPlayer, player);
        this.gobanSize = board.getBoardSize();
    }

    public Point isoToGoban (Point isoCoords){
        //If isoCoords.x too small or too big, or if isoCoords.y too small or too big
        return null;
    }

    public GameBoard getBoard() {
        return board;
    }
}
