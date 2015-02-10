package com.ragego.engine;

/**
 * This is the piece with which you play.
 * @todo Write some docs here
 */
public class Stone {
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
     * @throws java.lang.IllegalArgumentException Stone is not on this board
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
     * @param position
     */
    public void setPosition(Intersection position) {
        this.position = position;
    }

    public Intersection getPosition() {
        return position;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }
}
