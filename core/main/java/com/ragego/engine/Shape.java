package com.ragego.engine;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Link {@link StoneGroup}s between to detect big group of stones.
 * <p>
 * A Shape is an ensemble of stone of the same player which are near.
 * Near means that the stones are on next intersection. It allows column - 1, column, column + 1 for column and same for
 * line.
 * </p>
 * <p>
 * This object is a one time use, it's not valid when changes occurs on the board.
 * This is useful for IA computing and for scoring.
 * </p>
 */
public class Shape {

    private final static HashMap<StoneGroup, Shape> shapes = new HashMap<StoneGroup, Shape>(GameBoard.DEFAULT_BOARD_SIZE * GameBoard.DEFAULT_BOARD_SIZE);

    private final ArrayList<StoneGroup> linkedGroups = new ArrayList<StoneGroup>();
    private final Player player;
    private final GameBoard gameBoard;

    private Shape(GameBoard board, StoneGroup firstGroup) {
        this.gameBoard = board;
        player = firstGroup.getPlayer();
        linkedGroups.add(firstGroup);
        if (shapes.containsKey(firstGroup))
            shapes.remove(firstGroup);
        shapes.put(firstGroup, this);
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
        for (int i = 0; i < gameBoard.getBoardSize(); i++) {
            int column = 0, line = 0;
            switch (border) {

                case RIGHT:
                    column = gameBoard.getBoardSize() - 1;
                    line = i;
                    break;
                case LEFT:
                    column = 0;
                    line = i;
                    break;
                case TOP:
                    column = i;
                    line = gameBoard.getBoardSize() - 1;
                    break;
                case BOTTOM:
                    column = i;
                    line = 0;
                    break;
            }
            final Stone stone = gameBoard.getElement(column, line);
            if (stone == null) continue;
            if (stone.getPlayer() != player) continue;
            if (linkedGroups.contains(stone.getStoneGroup()))
                count++;
        }
        return count;
    }

    /**
     * Compute the Shape from a starting shape.
     * @param stoneGroup The stone group to start compute.
     * @return The shape corresponding to this group.
     */
    public static Shape getShape(StoneGroup stoneGroup) {
        if (hasComputedShape(stoneGroup))
            return shapes.get(stoneGroup);
        final Shape shape = new Shape(stoneGroup.getBoard(), stoneGroup);
        shape.searchGroups(stoneGroup);
        return shape;
    }

    /**
     * Find if this group has a computed shape.
     *
     * @param stoneGroup The stone group which you want to check.
     * @return true if we have a computed shape.
     */
    public static boolean hasComputedShape(StoneGroup stoneGroup) {
        return shapes.containsKey(stoneGroup);
    }

    /**
     * Recursive search for shapes.
     * Improved method to look for shapes.
     * @param stoneGroup The stone group which we are looking for groups around.
     */
    private void searchGroups(StoneGroup stoneGroup) {
        for (Stone stone : stoneGroup.getStones()) {
            final Intersection stoneIntersection = stone.getPosition();
            for (int deltaColumn = -1; deltaColumn < 2; deltaColumn += 2)
                for (int deltaLine = -1; deltaLine < 2; deltaLine += 2) {
                    final Intersection checkedIntersection = Intersection.get(
                            stoneIntersection.getColumn() + deltaColumn,
                            stoneIntersection.getLine() + deltaLine,
                            stoneIntersection.getBoard());
                    final Stone checkedStone = gameBoard.getElement(checkedIntersection);
                    if (checkedStone == null) continue;
                    if (checkedStone.getPlayer() != player) continue;
                    final StoneGroup checkedStoneStoneGroup = checkedStone.getStoneGroup();
                    if (!linkedGroups.contains(checkedStoneStoneGroup)) {
                        linkedGroups.add(checkedStoneStoneGroup);
                        shapes.put(checkedStoneStoneGroup, this);
                        searchGroups(checkedStoneStoneGroup);
                    }
                }
        }
    }

}
