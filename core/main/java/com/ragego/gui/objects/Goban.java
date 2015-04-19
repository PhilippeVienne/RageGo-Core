package com.ragego.gui.objects;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.ragego.engine.GameBoard;
import com.ragego.engine.HumanPlayer;
import com.ragego.gui.screens.GoGameScreen;

public class Goban {
    private final GoGameScreen screen;
    private final TiledMap map;
    private final TiledMapTileLayer gridLayer;
    private final Vector2 gobanOriginCoords;
    private final int gobanSize;
    private final GameBoard board;

    public Goban(GoGameScreen screen, TiledMap map, HumanPlayer player1, HumanPlayer player2) {
        this.screen = screen;
        this.map = screen.getMap();
        this.gridLayer = (TiledMapTileLayer) map.getLayers().get("grid");

        gobanOriginCoords = new Vector2(Float.parseFloat(map.getProperties().get("goOriginX", String.class)),
                Float.parseFloat(map.getProperties().get("goOriginY", String.class)));

        this.gobanSize = Integer.parseInt(map.getProperties().get("gobanSize", String.class));
        board = new GameBoard(player1, player2, gobanSize);
    }

    public Vector2 isoToGoban (Vector2 isoCoords){
        //If isoCoords.x too small or too big, or if isoCoords.y too small or too big
        return null;
    }

    public GameBoard getBoard() {
        return board;
    }
}
