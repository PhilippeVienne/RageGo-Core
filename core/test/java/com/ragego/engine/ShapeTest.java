package com.ragego.engine;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit testing for shapes.
 */
public class ShapeTest {

    private GameBoard gameBoard;

    @Before
    public void setUp() throws Exception {
        gameBoard = new GameBoard(new TestPlayer(), new TestPlayer());
    }

    @Test
    public void testIsOnBorder() throws Exception {
        getBlacks().registerNodeToPlay(gameBoard, "a1", "b2", "a3");
        getWhites().registerNodeToPlay(gameBoard, "a2", "b10", "b3");
        while (getBlacks().hasNextNode() || getWhites().hasNextNode())
            gameBoard.nextMove();
        assertNotNull(gameBoard.getElement(0, 0));
        assertNotNull(gameBoard.getElement(0, 0).getStoneGroup());
        assertEquals(gameBoard.getElement(0, 0).getStoneGroup().getStones().size(), 1);
        final Shape shape = Shape.getShape(gameBoard.getElement(0, 0).getStoneGroup());
        assertTrue(shape.isOnBorder(Border.TOP));
        assertTrue(shape.isOnBorder(Border.LEFT));
    }

    @Test
    public void testCountOnBorder() throws Exception {
        getBlacks().registerNodeToPlay(gameBoard, "a1", "b2", "a3");
        getWhites().registerNodeToPlay(gameBoard, "a2", "b10", "b3");
        while (getBlacks().hasNextNode() || getWhites().hasNextNode())
            gameBoard.nextMove();
        final Shape shape = Shape.getShape(gameBoard.getElement(0, 0).getStoneGroup());
        assertEquals(shape.countOnBorder(Border.LEFT), 2);
    }

    private TestPlayer getBlacks() {
        return (TestPlayer) gameBoard.getBlackPlayer();
    }

    private TestPlayer getWhites() {
        return (TestPlayer) gameBoard.getWhitePlayer();
    }
}