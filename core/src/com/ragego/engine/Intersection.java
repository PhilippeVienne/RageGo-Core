package com.ragego.engine;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A coordinate on the GameBoard.
 * @author Philippe Vienne
 */
public class Intersection {

    private static HashMap<Integer, Intersection> intersections = new HashMap<Integer, Intersection>(GameBoard.DEFAULT_BOARD_SIZE * GameBoard.DEFAULT_BOARD_SIZE);

    private int column;
    private int line;
    private GameBoard board;

    /**
     * Create a representation of an Intersection with an attached board.
     * @param column Column associated : numbers from left to right
     * @param line Line associated : numbers from top to bottom
     * @param board Attached board
     */
    protected Intersection(int column, int line, GameBoard board) {
        this.column = column;
        this.line = line;
        this.board = board;
        intersections.put(hashCode(), this);
    }

    /**
     * Compute an unique identifier for an intersection.
     * This function suppose that line and column are inferior to 100.
     *
     * @param column Column associated to this intersection
     * @param line   Line associated to this intersection
     * @param board  Board where the intersection is
     * @return An unique (probably not granted) identifier
     */
    private static int computeUniqueKey(int column, int line, GameBoard board) {
        return (board != null ? board.hashCode() * 10000 : 0) + line * 100 + column;
    }

    /**
     * Retrive an intersection.
     * If the intersection has already been created, return it. Otherwise, create a new Intersection.
     *
     * @param column Column associated : numbers from left to right
     * @param line   Line associated : numbers from top to bottom
     * @param board  Attached board
     * @return The representation of the intersection
     */
    public static Intersection get(int column, int line, GameBoard board) {
        if (intersections.containsKey(computeUniqueKey(column, line, board)))
            return intersections.get(computeUniqueKey(column, line, board));
        else
            return new Intersection(column, line, board);
    }

    /**
     * Retrieve an intersection from a SGF format.
     *
     * @param coordinate Two letters string which represent the coordinate.
     * @param board      Attached board
     * @return Representation of the intersection
     */
    public static Intersection get(String coordinate, GameBoard board) {
        coordinate = coordinate.toLowerCase();
        if (coordinate.matches("[a-t][0-9]{1,2}")) {
            return Intersection.get(
                    ((int) (coordinate.split("[0-9]", 2)[0].charAt(0))) - 'a',
                    Integer.parseInt(coordinate.substring(1, coordinate.length())) - 1,
                    board);
        } else if (coordinate.matches("[a-t]{2}")) {
            int column = ((int) coordinate.charAt(0)) - 'a', line = ((int) coordinate.charAt(1)) - 'a';
            return get(column, line, board);
        } else { // It's not a SGF standard format
            throw new IllegalArgumentException("Can not create an intersection with coordinate " + coordinate);
        }

    }

    /**
     * The column associated with this intersection.
     * Columns are numbers from left to right
     * @return The associated column
     */
    public int getColumn() {
        return column;
    }

    /**
     * The line associated with this intersection.
     * Lines are numbers from top to bottom
     * @return The associated line
     */
    public int getLine() {
        return line;
    }

    /**
     * The board where this intersection is.
     * @return The associated board
     */
    public GameBoard getBoard() {
        return board;
    }

    /**
     * Compare Intersections.
     * Object and this intersection are identical means that object is an
     * Intersection and that column, line and board are identical.
     * @param o Intersection to compare
     * @return true if they are identical.
     */
    @Override
    public boolean equals(Object o) {
        return o != null && o instanceof Intersection && hashCode() == o.hashCode();
    }

    /**
     * Test if a position is neighbour to this one.
     *     A neighbour is a position on :
     * <ul>
     *     <li>column less 1</li>
     *     <li>column plus 1</li>
     *     <li>line plus 1</li>
     *     <li>line less 1</li>
     * </ul>
     * <p>This function has the same result for a.isAsideOf(b) and
     * b.isAsideOf(a)</p>
     * @param position The position to test
     * @return true if they are neighbours
     */
    public boolean isAsideOf(Intersection position) {
        if(position == null)
            throw new IllegalArgumentException("Position is null");
        return ((line - 1) == position.line && column == position.column) ||
                ((line + 1) == position.line && column == position.column) ||
                ((column - 1) == position.column && line == position.line) ||
                ((column + 1) == position.column && line == position.line);
    }

    /**
     * Get neighbours of this intersection.
     * @return Array of intersection. The size depends of number of neighbours
     */
    public ArrayList<Intersection> getNeighboursIntersections() {
        ArrayList<Intersection> neighbours = new ArrayList<Intersection>(4);
        for(Intersection i:new Intersection[]{
                get(column+1,line,board),
                get(column-1,line,board),
                get(column,line+1,board),
                get(column,line-1,board),
        }) if(board.isValidIntersection(i)) neighbours.add(i);
        return neighbours;
    }

    @Override
    public int hashCode() {
        return computeUniqueKey(column,line,board);
    }


}
