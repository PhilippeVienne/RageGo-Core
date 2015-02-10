package com.ragego.engine;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test for {@link com.ragego.engine.Intersection}
 */
public class IntersectionTest {

    private final static GameBoard g1 = FakeGenerator.generateSimpleBoard();
    private final static GameBoard g2 = FakeGenerator.generateSimpleBoard();
    private final static Intersection i1 = FakeGenerator.generateIntersection(1,2,g1);
    private final static Intersection i2 = FakeGenerator.generateIntersection(1,2,g2);

    @Test
    public void testGetters() throws Exception {
        assertEquals("Column is not good",1,i1.getColumn());
        assertEquals("Column is not good",1,i2.getColumn());
        assertEquals("Line is not good",2,i1.getLine());
        assertEquals("Line is not good",2,i2.getLine());
    }

    @Test
    public void testEqualsFailsBetweenTypes() throws Exception {
        assertNotSame(i1, "123");
    }

    @Test
    public void testEqualsFailsBetweenGameBoard() throws Exception {
        assertNotSame(i1, i2);
    }

    @Test
    public void testEqualsWorks() throws Exception {
        assertEquals(i1,new Intersection(i1.getColumn(),i1.getLine(),i1.getBoard()));
    }
}
