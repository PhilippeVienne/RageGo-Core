package com.ragego.enginge;


import com.ragego.engine.GameBoard;
import com.ragego.engine.Player;
import org.junit.Test;

/**
 * Class created to check that dead stones are dead.
 * The test bring a board with dead stones and check the compute.
 */
public class DeadStonesTest {

    public static void main(String[] args){
        GameBoard board = new GameBoard(new TestPlayer(),new TestPlayer());
    }

    @Test
    public void testMe() {
        System.out.println("I'm tested !");
    }

    private static class TestPlayer extends Player{

        @Override
        public String getDisplayName() {
            return "Test player";
        }
    }

}
