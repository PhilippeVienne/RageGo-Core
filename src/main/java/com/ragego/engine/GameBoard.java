package com.ragego.engine;

import java.util.HashMap;

/**
 * Represent a board of Go.
 *
 * On this board, intersections are represented with numerical coordinates :
 * <ul>
 *  <li>The first coordinate is the column from left to right.</li>
 *  <li>The second coordinate is the line from top to bottom.</li>
 * </ul>
 * <p>Each intersection is assigned to a shape, empty (or assigned to something special)</p>
 *
 * @author Philippe Vienne
 */
public class GameBoard {

    /**
     * Default size for a Go board.
     */
    public static final int DEFAULT_BOARD_SIZE = 19;

    private Player firstPlayer;
    private Player secondPlayer;
    private int boardSize = DEFAULT_BOARD_SIZE;

    /**
     * Store the elements on bord by {@link com.ragego.engine.Intersection}
     */
    private HashMap<Intersection, GoElement> board;

    /**
     * Create an real empty board.
     * This constructor is suitable only for test purpose. 
     */
    public GameBoard() {
        this(null, null,DEFAULT_BOARD_SIZE);
    }

    /**
     * Create a board with the default size. 
     * @param firstPlayer The first player (conventional white stone player)
     * @param secondPlayer The second player (conventional black stone player)
     */
    public GameBoard(Player firstPlayer, Player secondPlayer) {
        this(firstPlayer, secondPlayer, DEFAULT_BOARD_SIZE);
    }

    /**
     * Create a board with a custom size.
     * @param firstPlayer The first player (conventional white stone player)
     * @param secondPlayer The second player (conventional black stone player)
     * @param boardSize Number of column for this board
     */
    public GameBoard(Player firstPlayer, Player secondPlayer, int boardSize) {
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
        this.boardSize = boardSize;
        this.board = new HashMap<>(this.boardSize*this.boardSize);
    }

    /**
     * Retrieve the size of this board.
     * @return The number of column and lines on board
     */
    public int getBoardSize() {
        return boardSize;
    }

    /**
     * Retrieve something on Intersection
     *
     * @param intersection The intersection where the element should be
     * @return The element or null if it's empty
     */
    public GoElement getElement(Intersection intersection) {
        if (intersection.getBoard() != this)
            throw new IllegalArgumentException("Not on good board");
        return board.getOrDefault(intersection, null);
    }

    /**
     * Retrieve the white player
     *
     * @return The player or null if it's empty
     */
    public Player getFirstPlayer() {
        return firstPlayer;
    }

    /**
     * Retrieve the black player.
     *
     * @return The player or null if it's empty
     */
    public Player getSecondPlayer() {
        return secondPlayer;
    }
}
