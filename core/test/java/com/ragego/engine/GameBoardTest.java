package com.ragego.engine;

import org.junit.Before;
import org.junit.Test;

public class GameBoardTest {

    private GameBoard gameBoard;
    private TestPlayer firstPlayer;
    private TestPlayer secondPlayer;

    @Before
    public void init() {
        gameBoard = new GameBoard();
    }

    @Test
    public void playerShouldNotEditingBoard() {

    }

    /**
     * Test player to simulate players on GameBoard.
     * This are simple players where you can register all turns to act.
     */
    private class TestPlayer extends Player {


        public TestPlayer() {

        }

        @Override
        public String getDisplayName() {
            return "TestPlayer" + hashCode();
        }
    }
}
