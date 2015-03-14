package com.ragego.engine;

import java.util.HashMap;

/**
 * This is the piece with which you play.
 * todo: Write some docs here
 */
public class Stone {

    private static HashMap<Intersection,Stone> stones = new HashMap<>();

    private GameBoard board;
    private boolean capturated=false;
    private Player player;
    private Intersection position;
    private Shape shape;

    /**
     * @return true if stone is on board
     */
    public boolean isOnBoard(){
        return board!=null&&position!=null&&shape!=null&&position.getBoard().equals(board);
    }

    /**
     * @param board The new linked board for this
     * @throws IllegalArgumentException Stone is not on this board
     */
    public void setBoard(GameBoard board) {
        if(position!=null&&position.getBoard()!=null&&!position.getBoard().equals(board))
            throw new IllegalArgumentException("Position is not on the good board");
        this.board = board;
    }

    /**
     * @return The linked board for this shape
     */
    public GameBoard getBoard() {
        return board;
    }

    /**
     * Update the postion af a stone an a board
     * @param p Position to set for this stone
     */
    public void setPosition(Intersection p) {
        this.position = p;
    }

    public Intersection getPosition() {
        return position;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public Player getPlayer() {
        return player;
    }

    public Shape getShape() {
        return shape;
    }

    /**
     * Count liberty for a stone.
     * For each neighbour position of this stone, liberty is increased of one if the intersection is empty.
     * @return The liberty count for this shape
     */
    public int countLiberty() {
        int liberty = 0;
        for (Intersection intersection : position.getNeighboursIntersections()) {
            if(board.isEmpty(intersection)) liberty++;
        }
        return liberty;
    }

    public void setCaptivated() {
        capturated = !capturated;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public static Stone get(Intersection intersection) {
        return null;
    }
}
