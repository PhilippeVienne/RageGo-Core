package com.ragego.engine;

import java.util.HashMap;

/**
 * This is the piece with which you play.
 * todo: Write some docs here
 */
public class Stone {

    private static HashMap<Intersection, Stone> stones = new HashMap<Intersection, Stone>();
    private final Player player;
    private final Intersection position;
    private GameBoard board;
    private boolean capturated = false;
    private Shape shape;

    public Stone() {
        this(null, null);
    }

    public Stone(Intersection intersection) {
        this(intersection, null);
    }

    public Stone(Intersection intersection, Player player) {
        this.position = intersection;
        this.player = player;
        if (intersection != null)
            this.board = intersection.getBoard();
    }

    public static Stone get(Intersection intersection) {
        return null;
    }

    /**
     * @return true if stone is on board
     */
    public boolean isOnBoard() {
        return board != null && position != null && shape != null && position.getBoard().equals(board);
    }

    /**
     * @return The linked board for this shape
     */
    public GameBoard getBoard() {
        return board;
    }

    /**
     * @param board The new linked board for this
     * @throws IllegalArgumentException Stone is not on this board
     */
    public void setBoard(GameBoard board) {
        if (position != null && position.getBoard() != null && !position.getBoard().equals(board))
            throw new IllegalArgumentException("Position is not on the good board");
        this.board = board;
    }

    public Intersection getPosition() {
        return position;
    }

    public Player getPlayer() {
        return player;
    }

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    /**
     * Count liberty for a stone.
     * For each neighbour position of this stone, liberty is increased of one if the intersection is empty.
     *
     * @return The liberty count for this shape
     */
    public int countLiberty() {
        int liberty = 0;
        for (Intersection intersection : position.getNeighboursIntersections()) {
            if (board.isEmpty(intersection) && board.isValidIntersection(intersection)) liberty++;
        }
        return liberty;
    }

    public void setCaptivated() {
        capturated = !capturated;
    }
}
