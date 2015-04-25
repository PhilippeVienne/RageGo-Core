package com.ragego.gui.objects;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Vector2;
import com.ragego.engine.GameBoard;
import com.ragego.engine.GameNode;
import com.ragego.engine.Intersection;
import com.ragego.engine.Stone;
import com.ragego.gui.screens.GoGameScreen;
import com.ragego.utils.GuiUtils;

import java.util.ArrayList;
import java.util.List;

public class Goban {
    private final GoGameScreen screen;
    private final TiledMap map;
    private final TiledMapTile blackStone;
    private final TiledMapTile whiteStone;
    private final Vector2 gobanOriginCoords;
    private final int gobanSize;
    private final Thread engineThread;
    private final List<GraphicStone> stones = new ArrayList<GraphicStone>(19 * 19);
    private GameBoard board;
    private GobanGameBoardListener listener;

    public Goban(GoGameScreen screen, TiledMap map) {
        this.screen = screen;
        this.map = screen.getMap();
        gobanOriginCoords = new Vector2(Float.parseFloat(map.getProperties().get("goOriginX", String.class)),
                Float.parseFloat(map.getProperties().get("goOriginY", String.class)));
        final TiledMapTileSet stoneTS = map.getTileSets().getTileSet("stoneTS");
        int firstGid = stoneTS.getProperties().get("firstgid", Integer.class);
        this.blackStone = stoneTS.getTile(firstGid);
        this.whiteStone = stoneTS.getTile(firstGid + 1);
        this.gobanSize = Integer.parseInt(map.getProperties().get("gobanSize", String.class));
        engineThread = new Thread(new GameRunnable());
    }

    public TiledMapTileLayer getGridLayer() {
        return (TiledMapTileLayer) map.getLayers().get("grid");
    }

    public TiledMapTileLayer getStoneLayer() {
        return (TiledMapTileLayer) map.getLayers().get("stones");
    }

    public void startGame() {
        if (board != null && !engineThread.isAlive())
            engineThread.start();
    }

    public void stopGame() {
        if (!engineThread.isInterrupted())
            engineThread.interrupt();
    }

    public Vector2 isoToGoban (Vector2 isoCoords){
        isoCoords.add(-gobanOriginCoords.x, -gobanOriginCoords.y);
        return isoCoords;
    }

    public Vector2 gobanToIso(Vector2 isoCoords) {
        isoCoords.add(gobanOriginCoords.x, gobanOriginCoords.y);
        return isoCoords;
    }

    public GameBoard getBoard() {
        return board;
    }

    public Vector2 waitForUserInputOnGoban() {
        final Vector2 vector2 = screen.waitForUserInputOnGoban();
        return isoToGoban(vector2);
    }

    private Vector2 getStonePositionOnMap(Intersection intersection) {
        return getVectorIsoTopToIsoRight(gobanToIso(new Vector2(intersection.getLine(), intersection.getColumn())));
    }

    private Vector2 getVectorIsoTopToIsoRight(Vector2 vector2) {
        return GuiUtils.isoTopToIsoLeft(vector2, map.getProperties().get("height", Integer.class));
    }

    public boolean isValidOnGoban(Vector2 vector2) {
        vector2 = isoToGoban(vector2);
        return vector2.x >= 0 && vector2.x < gobanSize && vector2.y >= 0 && vector2.y < gobanSize;
    }

    public int getSize() {
        return gobanSize;
    }

    public void setGameBoard(GameBoard gameBoard) {
        if (this.board != null)
            this.board.removeGameListener(this.listener);
        this.listener = new GobanGameBoardListener(this);
        gameBoard.addGameListener(listener);
        this.board = gameBoard;
    }

    public TiledMapTile getBlackStoneTile() {
        return blackStone;
    }

    public TiledMapTile getWhiteStoneTile() {
        return whiteStone;
    }

    public void addGraphicStone(GraphicStone graphicStone) {
        if (stones.contains(graphicStone)) return;
        stones.add(graphicStone);
        getStoneCell(graphicStone.getIntersection()).setTile(graphicStone.getStoneTile());
    }

    private TiledMapTileLayer.Cell getStoneCell(Intersection intersection) {
        Vector2 position = getStonePositionOnMap(intersection);
        TiledMapTileLayer.Cell cell = getStoneLayer().getCell((int) position.x, (int) position.y);
        if (cell == null) {
            cell = new TiledMapTileLayer.Cell();
            getStoneLayer().setCell((int) position.x, (int) position.y, cell);
        }
        return cell;
    }

    public GraphicStone getGraphicStone(Stone stone) {
        for (GraphicStone gs : stones) {
            if (stone.equals(gs.getStone()))
                return gs;
        }
        return null;
    }

    public void removeGraphicStone(GraphicStone graphicStone) {
        if (!stones.contains(graphicStone)) return;
        stones.remove(graphicStone);
        getStoneCell(graphicStone.getIntersection()).setTile(null);
    }

    public void animate(GameNode node) {
        // NOT SUPPORTED YET
    }

    public void updateCurrentPlayer() {
        // NOT SUPPORTED YET
    }

    private class GameRunnable implements Runnable {
        @Override
        public void run() {
            board.nextMove();
            if (!engineThread.isInterrupted())
                run();
        }
    }
}
