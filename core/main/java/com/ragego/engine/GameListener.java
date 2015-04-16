package com.ragego.engine;

/**
 * Listener for a Game.
 * This interface is designed to be warned about what's happening in a game.
 * This is useful for the GUI and networking functions.
 */
public interface GameListener extends TurnListener {

    /**
     * Informs a Stone is added on the board.
     * This function is called before dead stones are computed. Take care, if an IA edit manually the board,
     * this function could not be call.
     *
     * @param stone The stone which was added.
     */
    void newStoneAdded(Stone stone);

    /**
     * Informs a Stone is removed from the board.
     * This function is called after the stone has been deleted.
     *
     * @param stone The stone which has been removed.
     */
    void stoneRemoved(Stone stone);

    /**
     * A player ask to play a node.
     *
     * @param node The node which player wants to play.
     */
    void playNode(GameNode node);

}
