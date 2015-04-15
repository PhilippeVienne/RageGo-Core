package com.ragego.engine;

/**
 * Element which can be referenced by one positions on a board game.
 */
public interface GoElementUnique extends GoElement {

    /**
     * Intersection used by this element.
     *
     * @return The intersection.
     */
    Intersection getPosition();
}
