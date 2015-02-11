package com.ragego.engine;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GameBoardTest {

    private static final int DEFAULT_BOARD_SIZE = 19;
    private static final int CUSTOM_BOARD_SIZE = 9;

    private Player player1;
    private Player player2;

    /**
     * Minimal implementation for Player to run tests 
     */
    private static class TestPlayer extends Player{
        
        private static int count = 0;
        private final String name;

        public TestPlayer(){
            this.name = "TestPlayer"+count;
            count++;
        }

        @Override
        public String getDisplayName() {
            return name;
        }
    }
    
    @Before
    public void setUp() throws Exception {
        player1 = new TestPlayer();
        player2 = new TestPlayer();
    }

    @Test
    public void testGetBoardSize() throws Exception {
        GameBoard gameBoard = new GameBoard();
        assertEquals("Default Size is 19", DEFAULT_BOARD_SIZE, gameBoard.getBoardSize());
        gameBoard = new GameBoard(player1,player2);
        assertEquals("Default Size is 19", DEFAULT_BOARD_SIZE, gameBoard.getBoardSize());
        gameBoard = new GameBoard(player1,player2,GameBoard.DEFAULT_BOARD_SIZE);
        assertEquals("Default Size is 19", DEFAULT_BOARD_SIZE, gameBoard.getBoardSize());
        gameBoard = new GameBoard(player1,player2,CUSTOM_BOARD_SIZE);
        assertNotSame("Personalized size", DEFAULT_BOARD_SIZE, gameBoard.getBoardSize());
        assertEquals("Should return the true size", CUSTOM_BOARD_SIZE, gameBoard.getBoardSize());
    }

    @Test
    public void testGetElement() throws Exception {
        // TODO : Write this test !
    }

    @Test
    public void testGetFirstPlayer() throws Exception {
        GameBoard board = new GameBoard(player1,player2);
        assertEquals("Player 1 should be first player", player1, board.getFirstPlayer());
        assertNotSame("Player 2 should not be first player", player2, board.getFirstPlayer());
        assertNotNull("First player should be not null", board.getFirstPlayer());
        board = new GameBoard();
        assertNull("First player should now be null",board.getFirstPlayer());
    }

    @Test
    public void testGetSecondPlayer() throws Exception {
        GameBoard board = new GameBoard(player1,player2);
        assertNotSame("Player 1 should not be second player", player1, board.getSecondPlayer());
        assertEquals("Player 2 should be second player", player2, board.getSecondPlayer());
        assertNotNull("Second player should be not null", board.getSecondPlayer());
        board = new GameBoard();
        assertNull("Second player should now be null",board.getSecondPlayer());

    }

    @After
    public void tearDown() throws Exception {
        
    }
}