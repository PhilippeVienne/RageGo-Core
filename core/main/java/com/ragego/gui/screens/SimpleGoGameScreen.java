package com.ragego.gui.screens;

import com.ragego.engine.GameBoard;
import com.ragego.engine.HumanPlayer;
import com.ragego.engine.TurnListener;
import com.ragego.gui.GraphicTurnListener;
import com.ragego.gui.objects.Goban;

/**
 * Implements a simple Go Screen to play in local.
 */
public class SimpleGoGameScreen extends GoGameScreen {

    @Override
    protected void setupGoban(Goban goban) {
        TurnListener listener = new GraphicTurnListener(this, goban);
        goban.setGameBoard(new GameBoard(new HumanPlayer("Player 1", listener), new HumanPlayer("Player 2", listener), goban.getSize()));
        goban.startGame();
    }

    @Override
    protected final String getMapToLoad() {
        return "Goban_9_world_test";
    }
}
