package com.ragego.engine;

import java.util.HashMap;

/**
 * Represent a board of Go.
 *
 * On this board, intersections are represented with numerical coordinates :
 * <ul>
 *  <li>The first coordinate is the column from left to right.</li>
 *  <li>The second coordinate is the line from top to bottom.</li>
 * </ul>
 * <p>Each intersection is assigned to a shape, empty (or assigned to something special)</p>
 *
 * @author Philippe Vienne
 */
public class GameBoard {

    private Player firstPlayer;
    private Player secondPlayer;

    /**
     * Store the elements on bord by {@link com.ragego.engine.Intersection}
     */
    private HashMap<Intersection,GoElement> board;

}
