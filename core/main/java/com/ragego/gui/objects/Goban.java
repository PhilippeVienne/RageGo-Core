package com.ragego.gui.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Vector2;
import com.ragego.engine.*;
import com.ragego.gui.screens.GoGameScreen;
import com.ragego.utils.GuiUtils;
import com.ragego.utils.StandardGameFormatIO;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Graphical Goban.
 * This class is the link between the engine package and the gui. It manage that the user can correctly play to the game
 * by placing and removing stone as the engine says.
 */
public class Goban {
    private final GoGameScreen screen;
    private final TiledMap map;
    private final TiledMapTile blackStone;
    private final TiledMapTile whiteStone;
    private final Vector2 gobanOriginCoords;
    private final int gobanSize;
    private final List<GraphicStone> stones = new ArrayList<GraphicStone>(19 * 19);
    private Thread engineThread;
    private GameBoard board;
    private GobanGameBoardListener listener;
    private boolean gameRunning = false;
    private boolean passTurn = false;

    /**
     * Create a new Goban
     * @param screen Screen where this Goban lives.
     * @param map The map to load for this Goban
     */
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
        engineThread = generateGameThread();
    }

    /**
     * Accessor for the stone layer
     * @return Layer where the stones are
     */
    public TiledMapTileLayer getStoneLayer() {
        return (TiledMapTileLayer) map.getLayers().get("stones");
    }

    /**
     * Start the game engine. Let the game be !
     */
    public void startGame() {
        if (board == null) return;
        if (board.isGameEnded()) {
            Gdx.app.log("Goban", "Game is already ended and score is " + board.getScoreCounter().formatResult());
            return;
        }
        gameRunning = true;
        if (!engineThread.isAlive())
            try {
                engineThread.start();
            } catch (Exception e) {
                Gdx.app.error("Game", "Could not start computing engine", e);
            }
    }

    /**
     * Stop the Game engine thread. It will lost all network data being received and data in inputs for the current
     * player.
     */
    public void stopGame() {
        gameRunning = false;
        if (!engineThread.isInterrupted()) {
            engineThread.interrupt();
            engineThread = generateGameThread();
        }
    }

    /**
     * Create a thread for the Game engine
     * @return A thread to run to compute a game.
     */
    private Thread generateGameThread() {
        final Thread thread = new Thread(new GameRunnable(), "GameEngine-Thread");
        thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                Gdx.app.error(t.getName(), e.getMessage(), e);
                stopGame();
            }
        });
        return thread;
    }

    /**
     * Convert iso top coordinates to goban coordinates
     * @param isoCoords Coordinates to convert
     * @return Coordinates on Goban (this function do not check that the coordinates are valid)
     */
    public Vector2 isoToGoban (Vector2 isoCoords){
        isoCoords.add(-gobanOriginCoords.x, -gobanOriginCoords.y);
        return isoCoords;
    }


    /**
     * Convert goban coordinates to iso top coordinates
     * @param isoCoords Coordinates to convert
     * @return Coordinates on iso top (this function do not check that the coordinates are valid)
     */
    public Vector2 gobanToIso(Vector2 isoCoords) {
        isoCoords.add(gobanOriginCoords.x, gobanOriginCoords.y);
        return isoCoords;
    }

    /**
     * Getter for the {@link GameBoard} used by this class
     * @return the board used in this class
     */
    public GameBoard getBoard() {
        return board;
    }

    /**
     * Wait until the user has entered a new entry on the Goban.
     * @return The coordinate where we can play or null if the user pass.
     */
    public Vector2 waitForUserInputOnGoban() {
        final Vector2 vector2 = screen.waitForUserInputOnGoban();
        if (vector2 == null) return null;
        else return isoToGoban(vector2);
    }

    /**
     * Getter for stone position.
     * @param intersection The intersection of the stone
     * @return The vector in iso left for the given intersection.
     */
    private Vector2 getStonePositionOnMap(Intersection intersection) {
        return getVectorIsoTopToIsoRight(gobanToIso(new Vector2(intersection.getLine(), intersection.getColumn())));
    }

    /**
     * Convert iso left to iso top coordinates for this board.
     * @param vector2 See {@link GuiUtils#isoTopToIsoLeft(Vector2, int)}
     * @see GuiUtils#isoTopToIsoLeft(Vector2, int)
     * @return Vector in isoTop coordinates
     */
    private Vector2 getVectorIsoTopToIsoRight(Vector2 vector2) {
        return GuiUtils.isoTopToIsoLeft(vector2, map.getProperties().get("height", Integer.class));
    }

    /**
     * Check if given coordinates are valid on this goban
     * @param vector2 Coordinates in isoTop
     * @see GuiUtils#isoLeftToIsoTop(Vector2, int)
     * @see #isoToGoban(Vector2)
     * @return true if this position is valid on this Goban.
     */
    public boolean isValidOnGoban(Vector2 vector2) {
        vector2 = isoToGoban(vector2);
        return vector2.x >= 0 && vector2.x < gobanSize && vector2.y >= 0 && vector2.y < gobanSize;
    }

    /**
     * Getter for board size.
     * @return The size for the graphic board.
     */
    public int getSize() {
        return gobanSize;
    }

    /**
     * Set the GameBoard to run with this graphic Goban.
     * @param gameBoard The board which we are supposed to work with.
     */
    public void setGameBoard(GameBoard gameBoard) {
        if (this.board != null)
            this.board.removeGameListener(this.listener);
        this.listener = new GobanGameBoardListener(this);
        gameBoard.addGameListener(listener);
        this.board = gameBoard;
        clearBoard();
    }

    /**
     * Getter for {@link #blackStone}
     * @return Tile used for black stone.
     */
    public TiledMapTile getBlackStoneTile() {
        return blackStone;
    }

    /**
     * Getter for {@link #whiteStone}
     * @return Tile used for white stones.
     */
    public TiledMapTile getWhiteStoneTile() {
        return whiteStone;
    }

    /**
     * Add a new graphic stone to this Goban
     * @param graphicStone The stone to add to this Goban.
     */
    public void addGraphicStone(GraphicStone graphicStone) {
        if (stones.contains(graphicStone)) return;
        stones.add(graphicStone);
        getStoneCell(graphicStone.getIntersection()).setTile(graphicStone.getStoneTile());
    }

    /**
     * Retrieve a cell for placing a stone.
     * @param intersection The intersection that should match for this stone.
     * @return The cell corresponding to intersection or throw an error.
     */
    private TiledMapTileLayer.Cell getStoneCell(Intersection intersection) {
        Vector2 position = getStonePositionOnMap(intersection);
        TiledMapTileLayer.Cell cell = getStoneLayer().getCell((int) position.x, (int) position.y);
        if (cell == null) {
            cell = new TiledMapTileLayer.Cell();
            getStoneLayer().setCell((int) position.x, (int) position.y, cell);
        }
        return cell;
    }

    /**
     * Retrieve a graphic stone from the engine stone which correspond.
     * @param stone The stone from engine to find.
     * @return The graphic stone on the board or null if not found on this Goban
     */
    public GraphicStone getGraphicStone(Stone stone) {
        for (GraphicStone gs : stones) {
            if (stone.equals(gs.getStone()))
                return gs;
        }
        return null;
    }

    /**
     * Remove a stone from the graphical board. It does not remove it from the engine.
     * @param graphicStone The stone to remove from the board.
     */
    public void removeGraphicStone(GraphicStone graphicStone) {
        if (!stones.contains(graphicStone)) return;
        stones.remove(graphicStone);
        getStoneCell(graphicStone.getIntersection()).setTile(null);
        refreshUserScore();
    }

    /**
     * Act an animation on a node
     * @param node The node to animate
     */
    public void animate(GameNode node) {
        // NOT SUPPORTED YET
    }

    /**
     * Reset fields for a new turn.
     */
    public void updateCurrentPlayer() {
        passTurn = false;
    }

    /**
     * Determine if the current turn should be passed.
     * @return true if we should pass the turn.
     */
    public boolean passTurn() {
        return passTurn;
    }

    /**
     * Register that the user asked to pass this turn.
     */
    public void markTurnAsShouldBePassed() {
        passTurn = true;
    }

    /**
     * Cancel the last turn on the game.
     */
    public void cancelLastTurn() {
        stopGame();
        board.removeLastNode();
        startGame();
    }

    /**
     * Remake the last turn if there is one.
     */
    public void remakeTurn() {
        stopGame();
        board.remakeNode();
        startGame();
    }

    /**
     * Save the game in the temp directory.
     */
    public void save() {
        try {
            File tmp = File.createTempFile("RageGoGame", ".sgf");
            System.out.println("File is " + tmp.getAbsolutePath() + "/" + tmp.getName());
            StandardGameFormatIO io = new StandardGameFormatIO(tmp, board);
            io.write();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Runnable to make an infinite loop for playing game.
     */
    private class GameRunnable implements Runnable {
        @Override
        public void run() {
            while (gameRunning && !board.isGameEnded())
                board.nextMove();
            Gdx.app.log("Goban", "Game is ended and score is " + board.getScoreCounter().formatResult());
        }
    }

    /**
     * Clean the board from all stones on it.
     */
    public void clearBoard(){
        for (int i = 0; i < getBoard().getBoardSize(); i++) {
            for (int j = 0; j < getBoard().getBoardSize(); j++) {
                getStoneCell(Intersection.get(i,j,getBoard())).setTile(null);
            }
        }
    }

    /**
     * Refresh the score from the {@link #board} on the screen.
     */
    public void refreshUserScore() {
        ScoreCounter scoreCounter = board.getScoreCounter();
        screen.getHexaFrameTop().updateCapturedWhiteStones(scoreCounter.getCaptivatedStonesByBlack());
        System.out.println("Black score "+scoreCounter.getCaptivatedStonesByBlack());
        screen.getHexaFrameTop().updateCapturedBlackStones(scoreCounter.getCaptivatedStonesByWhite());
        System.out.println("White score "+scoreCounter.getCaptivatedStonesByWhite());
    }
}
