package com.ragego.gui.objects;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.ragego.engine.GameBoard;
import com.ragego.engine.HumanPlayer;
import com.ragego.gui.GraphicTurnListener;
import com.ragego.gui.screens.GoGameScreen;
import com.ragego.utils.DebugUtils;

public class Goban {
    private final GoGameScreen screen;
    private final TiledMap map;
    private final TiledMapTileLayer gridLayer;
    private final Vector2 gobanOriginCoords;
    private final int gobanSize;
    private final GameBoard board;
    private final Thread engineThread;

    public Goban(GoGameScreen screen, TiledMap map) {
        this.screen = screen;
        this.map = screen.getMap();
        this.gridLayer = (TiledMapTileLayer) map.getLayers().get("grid");

        gobanOriginCoords = new Vector2(Float.parseFloat(map.getProperties().get("goOriginX", String.class)),
                Float.parseFloat(map.getProperties().get("goOriginY", String.class)));

        this.gobanSize = Integer.parseInt(map.getProperties().get("gobanSize", String.class));
        board = new GameBoard(new HumanPlayer("Player 1", new GraphicTurnListener(screen, this)), new HumanPlayer("Player 2", new GraphicTurnListener(screen, this)), gobanSize);
        engineThread = new Thread(new GameRunnable());
    }

    public void startGame() {
        engineThread.start();
    }

    public void stopGame() {
        engineThread.interrupt();
    }

    public Vector2 isoToGoban (Vector2 isoCoords){
        //If isoCoords.x too small or too big, or if isoCoords.y too small or too big
        return null;
    }

    public GameBoard getBoard() {
        return board;
    }

    private class GameRunnable implements Runnable {
        @Override
        public void run() {
            board.nextMove();
            DebugUtils.printBoard(board);
            if (!engineThread.isInterrupted())
                run();
        }
    }
}
