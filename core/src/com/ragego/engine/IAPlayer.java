package com.ragego.engine;

import java.util.HashMap;

/**
 * Special functions for IAPlayers.
 * In RageGo, IA are not standard players, they can cheat. But they cheat differently so there is an interface to
 * aggregate functions.
 */
public interface IAPlayer {

    /**
     * Define a special turn which a IAPlayer can made when he wants.
     * When this function is called, it's like a god mode, IA can edit the GameBoard as it wants.
     */
    void makeSpecialTurn();

}
