package com.ragego.enginge;


import com.ragego.engine.*;

/**
 * Class created to check that dead stones are dead.
 * The test bring a board with dead stones and check the compute.
 */
public class DeadStonesTest {

    public static void main(String[] args){
        GameBoard board = new GameBoard(new TestPlayer(),new TestPlayer());
        board.setElement(Intersection.get(1,1,board), Stone.get(Intersection.get(1, 1, board)));
    }

    private static class TestPlayer extends Player{

        @Override
        public String getDisplayName() {
            return "Test player";
        }
    }

}
