package com.ragego.engine;

/**
 * Listen to know when to play.
 */
public interface TurnListener {

    /**
     * Run app_logic for this turn.
     * When game engine end to call each turn listener, the turn is ended. 
     * @param board The board on which we are playing
     * @param player The player who have to play
     */
    public void newTurn(GameBoard board, Player player);

    /**
     * Run an ends of turn.
     * This function is call at an end of turn. We suppose that you won't edit
     * the game. Score and state are already computed. 
     * @param board The board on which we are playing
     * @param player The player who has played
     * @param nextPlayer The player who will play
     */
    public void endOfTurn(GameBoard board, Player player, Player nextPlayer);

    /**
     * Run a start of turn.
     * This function is call at an end of turn. We suppose that you won't edit
     * the game. Score and state are already computed.
     * @param board The board on which we are playing
     * @param player The player who will played
     * @param previousPlayer The player who has played
     */
    public void startOfTurn(GameBoard board, Player player, Player previousPlayer);
    
}
