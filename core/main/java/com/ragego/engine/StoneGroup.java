package com.ragego.engine;

import java.util.ArrayList;

/**
 * StoneGroup is the configuration of stones in their flexibility and efficiency
 * at staying connected, forming eyes, and maintaining liberties.
 * <p>
 * Stones are
 * said to have good shape if they are efficient and flexible, or bad shape
 * if they are inefficient. Classic examples of good shape are the ponnuki
 * (four stones in a diamond created by capturing an enemy stone) and the
 * bamboo joint (a 2x3 pattern of two stones, two spaces and two more stones).
 * Examples of bad shape are the empty triangle (three adjacent stones forming
 * an 'L') and the dango (large clump of stones not containing any eyes).
 * Joseki is, in large part, the study of forming good stoneGroups with the stones.
 * </p>
 * <a href="http://en.wikipedia.org/wiki/Shape_%28Go%29">See Wikipedia</a>
 *
 * @author Philippe Vienne
 */
public class StoneGroup {

    private ArrayList<Stone> stones = new ArrayList<Stone>();
    private GameBoard board;
    private Player player;

    public StoneGroup(Player player, GameBoard board, Stone... stones) {
        setPlayer(player);
        setBoard(board);
        addStones(stones);
    }

    /**
     * Add multiple stones to the shape.
     * Used for creation
     *
     * @param stones Stones to add
     */
    private void addStones(Stone[] stones) {
        for (Stone s : stones)
            addStone(s);
    }

    /**
     * Add a stone to this shape.
     *
     * @param stone The stone to add to this shape
     * @return This shape
     */
    public StoneGroup addStone(Stone stone) {
        canStoneBeAdded(stone);
        if (!stones.contains(stone))
            stones.add(stone);
        if (stone.getStoneGroup() != this)
            stone.setStoneGroup(this);
        return this;
    }

    /**
     * Check if a stone can be a part of this shape.
     * A stone can be added, if it belongs to one of liberty of current stones.
     * Otherwise, the shape should be empty.
     *
     * @param stone The stone to test
     * @throws IllegalArgumentException The stone cannot be added to this shape
     */
    private void canStoneBeAdded(Stone stone) {
        //if(stones.isEmpty()) return;
        //if(stone.getPlayer()!=player||!isOnShapeLiberty(stone))
        //    throw new IllegalArgumentException("Can not be added to shape");
    }

    /**
     * Check if a stone is on liberty on one stone of this shape.
     *
     * @param stone The stone to test
     * @return true if it's on on liberty
     */
    private boolean isOnShapeLiberty(Stone stone) {
        final Intersection stonePosition = stone.getPosition();
        for (Stone s : stones) {
            if (s.getPosition().isAsideOf(stonePosition)) {
                return true;
            }
        }
        return false;
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
     *
     * @param board The new board to link this shape
     */
    public void setBoard(GameBoard board) {
        for (Stone stone : stones)
            stone.setBoard(board);
        this.board = board;
    }

    /**
     * Fusion multiple stoneGroups.
     *
     * @param stoneGroup the stoneGroup to fusion
     */
    public void unionWith(StoneGroup stoneGroup) {
        for (Stone stone : stoneGroup.stones.toArray(new Stone[stoneGroup.stones.size()])) {
            addStonePrivate(stone);
        }
    }

    /**
     * Force add of stone to the shape
     *
     * @param stone the stone to add
     */
    private void addStonePrivate(Stone stone) {
        stones.add(stone);
        stone.setStoneGroup(this);
    }

    /**
     * Count liberty of a shape.
     *
     * @return Number of liberty of shape
     */
    public int countLiberty() {
        int liberty = 0;
        for (Stone stone : stones) {
            if (stone != null)
                liberty += stone.countLiberty();
        }
        return liberty;
    }

    /**
     * Detect if this shape is dead.
     * A shape is alive only if its liberty is not null and contains more than one stone.
     *
     * @return true if you should consider this shape alive
     */
    public boolean isAlive() {
        return stones.size() > 0 && countLiberty() > 0;
    }

    public ArrayList<Stone> getStones() {
        return stones;
    }

    public void removeStone(Stone stone) {
        stones.remove(stone);
    }

    public Player getPlayer() {
        return player;
    }

    /**
     * Update the player of this shape.
     *
     * @param player The owner of this shape
     */
    private void setPlayer(Player player) {
        this.player = player;
    }

    public boolean isOnLiberty(StoneGroup comparedStoneGroup) {
        for (Stone stone : stones) {
            for (Stone stoneOfOtherShape : comparedStoneGroup.stones) {
                if (stone.isOnLiberty(stoneOfOtherShape) && stone.getPlayer() == stoneOfOtherShape.getPlayer())
                    return true;
            }
        }
        return false;
    }

    /**
     * Copy the shape and stones to a new board.
     * This function does not add stones to board, do not forget to do it.
     */
    public StoneGroup copy(GameBoard board) {
        StoneGroup stoneGroup = new StoneGroup(player, board);
        for (Stone stone : stones)
            stoneGroup.addStonePrivate(stone.copy(board));
        return stoneGroup;
    }

    /**
     * Determine is this group has one or more stones on a Border.
     *
     * @return true if it's on the border
     */
    public boolean isOnBorder(Border border) {
        for (Stone stone : stones)
            if (stone.isOnBorder(border))
                return true;
        return false;
    }

    /**
     * Check if this group is aside another.
     *
     * @param group The group to compare.
     * @return true if group is aside of this group.
     */
    public boolean isAsideOf(final StoneGroup group) {
        if (group.getBoard() != board) return false;
        final ArrayList<Intersection> visitedIntersection = new ArrayList<>(stones.size() * 4);
        for (Stone stone : stones) {
            final Intersection stoneIntersection = stone.getPosition();
            for (int deltaColumn = -1; deltaColumn < 2; deltaColumn += 2)
                for (int deltaLine = -1; deltaLine < 2; deltaLine += 2) {
                    final Intersection checkedIntersection = Intersection.get(
                            stoneIntersection.getColumn() + deltaColumn,
                            stoneIntersection.getLine() + deltaLine,
                            stoneIntersection.getBoard());
                    if (visitedIntersection.contains(checkedIntersection)) continue;
                    visitedIntersection.add(checkedIntersection);
                    final Stone supposedStone = board.getElement(checkedIntersection);
                    if (supposedStone == null) continue;
                    if (supposedStone.getStoneGroup() == group) return true;
                }
        }
        return false;
    }
}
