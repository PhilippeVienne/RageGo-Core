package com.ragego.engine;

/**
 * A coordinate on the GameBoard.
 * @author Philippe Vienne
 */
public class Intersection {

    private int column;
    private int line;
    private GameBoard board;

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
     * Create a representation of an Intersection without a board.
     * @param column Column associated : numbers from left to right
     * @param line Line associated : numbers from top to bottom
     * @deprecated It's bad to use without board, what are you doing ?
     */
    public Intersection(int column, int line) {
        this(column,line,null);
    }

    /**
     * Create a representation of an Intersection with an attached board.
     * @param column Column associated : numbers from left to right
     * @param line Line associated : numbers from top to bottom
     * @param board Attached board
     */
    public Intersection(int column, int line, GameBoard board) {
        this.column = column;
        this.line = line;
        this.board = board;
    }

    /**
     * Compare Intersections.
     * Object and this intersection are identical means that object is an
     * Intersection and that column, line and board are identical.
     * @param o Intersection to compare
     * @return true if they are identical.
     */
    @Override
    public boolean equals(Object o){
        if(o==null) return false; // the instance could not be equal to a null entry
        if(o instanceof Intersection){
            boolean boards = false;
            if(board!=null&&((Intersection) o).board!=null) // If they are not null
                boards = board.equals(((Intersection) o).board); // Then board should be equals
            else // Otherwise boards should be both null
                boards = (board == null && ((Intersection) o).board == null);
            return  boards &&
                    ((Intersection) o).column == column && // Columns identical
                    ((Intersection) o).line == line; // Line identical
        } else {
            return false; // an intersection could not be equal to other type than Intersection
        }
    }

}
