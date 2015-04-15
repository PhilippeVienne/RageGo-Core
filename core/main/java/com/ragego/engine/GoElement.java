package com.ragego.engine;

import java.util.ArrayList;

/**
 * Element which can be referenced by one or many positions on a board game.
 */
public interface GoElement {

    /**
     * Intersections used by this element
     *
     * @return List of used intersections
     */
    public ArrayList<Intersection> getPositions();

}
