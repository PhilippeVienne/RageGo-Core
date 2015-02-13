package com.ragego.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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

    /**
     * This params contain an historic of all know situation
     */
    private ArrayList<BoardSnap> snapshots = new ArrayList<>();

    private Player firstPlayer;
    private Player secondPlayer;
    private int boardSize = DEFAULT_BOARD_SIZE;

    /**
     * Store the elements on bord by {@link com.ragego.engine.Intersection}
     */
    private HashMap<Intersection, Stone> board;

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
     * Check that we can play on this intersection.
     * @param player Player which is playing
     * @param intersection Intersection where we want to play
     * @return true if it's correct following the Go rules to play on this row.
     */
    public boolean canPlay(Player player, Intersection intersection) throws GoRuleViolation{
        checkBoardForIntersection(intersection);
        if(null != getElement(intersection)) return false;

        return false;
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
    public Stone getElement(Intersection intersection) {
        checkBoardForIntersection(intersection);
        return board.getOrDefault(intersection, null);
    }

    /**
     * Check that intersection is on the current board.
     * @param intersection The intersection to check
     * @throws com.ragego.engine.GameBoard.BadBoardException If it's not the good board
     */
    private void checkBoardForIntersection(Intersection intersection) throws BadBoardException{
        if (intersection.getBoard() != this)
            throw new BadBoardException();
    }

    /**
     * Check and remove dead stones.
     * @return The dead stones
     */
    public Stone[] computeDeadStone(){
        ArrayList<Stone> deads = new ArrayList<>();

        return deads.toArray(new Stone[deads.size()]);
    }

    /**
     * Put an element to an intersection.
     * This function don't check that the element checks go rules. 
     * @param intersection One of intersection of this board
     * @param element The element to put
     */
    public void setElement(Intersection intersection, Stone element){
        checkBoardForIntersection(intersection);
        Shape shape = searchForShapesAround(intersection, element.getPlayer());
        if(shape == null)
            shape = new Shape(element.getPlayer(),this,element);
        else
            shape.addStone(element);
        element.setShape(shape);
        board.put(intersection,element);
    }

    /**
     * Lookup for a shape which can be connected to an intersection.
     * If there is multiple shapes which can be connected, return a fusion of
     * them.
     * @param intersection The intersection where we lookup
     * @param player The player which should be the owner of shape
     * @return The shape to associate with or null if there is not one.
     */
    private Shape searchForShapesAround(Intersection intersection, Player player) {
        ArrayList<Shape> shapes = new ArrayList<>(4);
        for (Intersection neighbours : intersection.getNeighboursIntersections()) {
            if(getElement(neighbours).getPlayer() == player) {
                Shape shape = getElement(neighbours).getShape();
                if(shape == null)
                    throw new IllegalStateException("A stone have no associated shape.");
                shapes.add(shape);
            }
        }
        Iterator<Shape> shapeIterator = shapes.iterator();
        switch (shapes.size()){
            case 0:
                return null;
            case 1:
                return shapeIterator.next();
            case 2:
            case 3:
            case 4:
                Shape newShape = shapeIterator.next();
                shapeIterator.forEachRemaining(newShape::unionWith);
                return  newShape;
        }
        return null; // We never never should go here
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

    /**
     * Create a primitive-type representation of the game.
     * @return A table where : 0 is an empty row, 1 for first player, 2 for second
     */
    public int[][] getRepresentation(){
        int[][] data = new int[boardSize][boardSize];
        board.forEach((intersection, goElement) -> {
            data[intersection.getColumn()][intersection.getLine()] =
                    goElement.getPlayer() == firstPlayer ?
                            1 : 2;
        });
        return data;
    }

    /**
     * Exception occurs when you are manipulating a wrong board.
     */
    public static class BadBoardException extends IllegalArgumentException{

        public BadBoardException() {
            super("Not on good board");
        }

        public BadBoardException(String s) {
            super(s);
        }

        public BadBoardException(String message, Throwable cause) {
            super(message, cause);
        }

        public BadBoardException(Throwable cause) {
            super("Not on good board",cause);
        }
    }
}
