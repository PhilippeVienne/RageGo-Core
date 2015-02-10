package com.ragego.engine;

import javax.swing.text.Position;
import java.util.ArrayList;

/**
 * Shape is the configuration of stones in their flexibility and efficiency
 * at staying connected, forming eyes, and maintaining liberties.
 * <p>
 *     Stones are
 * said to have good shape if they are efficient and flexible, or bad shape
 * if they are inefficient. Classic examples of good shape are the ponnuki
 * (four stones in a diamond created by capturing an enemy stone) and the
 * bamboo joint (a 2x3 pattern of two stones, two spaces and two more stones).
 * Examples of bad shape are the empty triangle (three adjacent stones forming
 * an 'L') and the dango (large clump of stones not containing any eyes).
 * Joseki is, in large part, the study of forming good shapes with the stones.
 * </p>
 * <a href="http://en.wikipedia.org/wiki/Shape_%28Go%29">See Wikipedia</a>
 * @author Philippe Vienne
 */
public class Shape implements GoElement{

    private ArrayList<Stone> stones = new ArrayList<>();
    private GameBoard board;


    @Override
    public ArrayList<Intersection> getPositions() {
        ArrayList<Intersection> pos = new ArrayList<>();
        for(Stone s:stones)
            pos.add(s.getPosition());
        return null;
    }

    /**
     * Add a stone to this shape.
     * @param stone
     * @return
     */
    public Shape addStone(Stone stone){
        canStoneBeAdded(stone);
        stone.setShape(this);
        stones.add(stone);
        return this;
    }

    private void canStoneBeAdded(Stone stone) {
        boolean isOnLiberty = isOnShapeLiberty(stone);
        if(!isOnLiberty&&!stones.isEmpty())
            throw new IllegalArgumentException("Can not be added to shape");
    }

    private boolean isOnShapeLiberty(Stone stone) {
        boolean isOnLiberty = false;
        for(Intersection p:getPositions()) {
            if (p.isAsideOf(stone.getPosition())) {
                isOnLiberty = true;
                break;
            }
        }
        return isOnLiberty;
    }

    /**
     * @return The linked board for this shape
     */
    public GameBoard getBoard() {
        return board;
    }

    /**
     * Edit the linked board for this shape.
     * Modify to itself and his child the linked board.
     * @param board The new board to link this shape
     */
    public void setBoard(GameBoard board) {
        for(Stone stone:stones)
            stone.setBoard(board);
        this.board = board;
    }
}
