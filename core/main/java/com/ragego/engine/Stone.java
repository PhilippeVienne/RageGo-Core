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
    private StoneGroup stoneGroup;

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
        return board != null && position != null && stoneGroup != null && position.getBoard().equals(board);
    }

    /**
     * @return The linked board for this stoneGroup
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

    public StoneGroup getStoneGroup() {
        return stoneGroup;
    }

    public void setStoneGroup(StoneGroup stoneGroup) {
        if (stoneGroup != null && !stoneGroup.getStones().contains(this)) {
            throw new IllegalArgumentException("StoneGroup does not contain stone");
        }
        this.stoneGroup = stoneGroup;
    }

    /**
     * Count liberty for a stone.
     * For each neighbour position of this stone, liberty is increased of one if the intersection is empty.
     *
     * @return The liberty count for this stoneGroup
     */
    public int countLiberty() {
        int liberty = 0;
        for (Intersection intersection : position.getNeighboursIntersections()) {
            if (!board.isValidIntersection(intersection)) continue;
            if (board.isEmpty(intersection)) {
                liberty++;
                continue;
            }
            final Stone element = board.getElement(intersection);
            if (element != null && element.getPlayer().equals(getPlayer()) && element.getStoneGroup() != stoneGroup)
                liberty++;
        }
        return liberty;
    }

    public void setCaptivated() {
        capturated = !capturated;
    }

    public boolean isOnLiberty(Stone otherStone) {
        for (Intersection intersection : position.getNeighboursIntersections()) {
            if (otherStone.position.equals(intersection))
                return true;
        }
        return false;
    }

    public Stone copy(GameBoard board) {
        return new Stone(position.forBoard(board), player);
    }
}
