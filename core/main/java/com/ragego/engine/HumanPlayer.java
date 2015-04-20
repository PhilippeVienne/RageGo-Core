package com.ragego.engine;

/**
 * Represent a human which play.
 */
public class HumanPlayer extends Player {

    private final String name;

    /**
     * Create a new Human player
     *
     * @param name His name (e.g.: Joe Doe)
     * @param listener The turn listener attached to this user.
     */
    public HumanPlayer(String name, TurnListener listener) {
        this.name = name;
        this.listener = listener;
    }

    @Override
    public String getDisplayName() {
        return name;
    }
}
