package com.ragego.engine;

import java.util.ArrayList;

/**
 * Link {@link StoneGroup}s between to detect big group of stones.
 * <p/>
 * A Shape is an ensemble of stone of the same player which are near.
 * Near means that the stones are on next intersection. It allows column - 1, column, column + 1 for column and same for
 * line.
 * <p/>
 * <p/>
 * This object is a one time use, it's not valid when changes occurs on the board.
 * This is useful for IA computing and for scoring.
 */
public class Shape {

    private final ArrayList<StoneGroup> linkedGroups = new ArrayList<StoneGroup>();
    private final Player player;
    private final GameBoard gameBoard;

    private Shape(GameBoard board, StoneGroup firstGroup) {
        this.gameBoard = board;
        player = firstGroup.getPlayer();
        linkedGroups.add(firstGroup);
    }

    /**
     * Determine if this shape touch a border of the screen.
     *
     * @param border The border wanted, see {@link Border}
     * @return True if one stone of this board is on this side.
     */
    public boolean isOnBorder(Border border) {
        for (StoneGroup group : linkedGroups)
            if (group.isOnBorder(border))
                return true;
        return false;
    }


    /**
     * Count number of unique touch this shape has on a border.
     * This count number of shapes touch the screens.
     *
     * @param border The border wanted, see {@link Border}
     * @return True if one stone of this board is on this side.
     */
    public int countOnBorder(Border border) {
        int count = 0;
        for (StoneGroup group : linkedGroups)
            if (group.isOnBorder(border))
                count++;
        return count;
    }

    /**
     * Compute the Shape from a starting shape.
     * @param stoneGroup The stone group to start compute
     */

}
