package com.ragego.engine;

<<<<<<< HEAD
=======
import javax.swing.text.Position;
>>>>>>> 7c128c15a804cac1c79c9c762749f87f5dd47695
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
<<<<<<< HEAD
    private Player player;

    public Shape(Player player, GameBoard board, Stone... stones) {
        setPlayer(player);
        setBoard(board);
        addStones(stones);
    }

    /**
     * Add multiple stones to the shape.
     * Used for creation 
     * @param stones Stones to add
     */
    private void addStones(Stone[] stones) {
        for(Stone s:stones)
            addStone(s);
    }
    
=======


>>>>>>> 7c128c15a804cac1c79c9c762749f87f5dd47695
    @Override
    public ArrayList<Intersection> getPositions() {
        ArrayList<Intersection> pos = new ArrayList<>();
        for(Stone s:stones)
            pos.add(s.getPosition());
<<<<<<< HEAD
        return pos;
=======
        return null;
>>>>>>> 7c128c15a804cac1c79c9c762749f87f5dd47695
    }

    /**
     * Add a stone to this shape.
<<<<<<< HEAD
     * @param stone The stone to add to this shape
     * @return This shape
=======
     * @param stone
     * @return
>>>>>>> 7c128c15a804cac1c79c9c762749f87f5dd47695
     */
    public Shape addStone(Stone stone){
        canStoneBeAdded(stone);
        stone.setShape(this);
        stones.add(stone);
        return this;
    }

<<<<<<< HEAD
    /**
     * Check if a stone can be a part of this shape.
     * A stone can be added, if it belongs to one of liberty of current stones.
     * Otherwise, the shape should be empty. 
     * @param stone The stone to test
     * @throws java.lang.IllegalArgumentException The stone cannot be added to this shape              
     */
    private void canStoneBeAdded(Stone stone) {
        if(stones.isEmpty()) return;
        if(stone.getPlayer()!=player||!isOnShapeLiberty(stone))
            throw new IllegalArgumentException("Can not be added to shape");
    }

    /**
     * Check if a stone is on liberty on one stone of this shape.
     * @param stone The stone to test
     * @return true if it's on on liberty
     */
=======
    private void canStoneBeAdded(Stone stone) {
        boolean isOnLiberty = isOnShapeLiberty(stone);
        if(!isOnLiberty&&!stones.isEmpty())
            throw new IllegalArgumentException("Can not be added to shape");
    }

>>>>>>> 7c128c15a804cac1c79c9c762749f87f5dd47695
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
<<<<<<< HEAD

    /**
     * Update the player of this shape. 
     * @param player The owner of this shape
     */
    private void setPlayer(Player player) {
        this.player = player;
    }
=======
>>>>>>> 7c128c15a804cac1c79c9c762749f87f5dd47695
}
