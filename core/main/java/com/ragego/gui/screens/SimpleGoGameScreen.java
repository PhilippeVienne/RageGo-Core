package com.ragego.gui.screens;

import com.ragego.engine.GameBoard;
import com.ragego.engine.HumanPlayer;
import com.ragego.engine.TurnListener;
import com.ragego.gui.GraphicTurnListener;

/**
 * Implements a simple Go Screen to play in local.
 */
public class SimpleGoGameScreen extends GoGameScreen {

    @Override
    public void show() {
        super.show();
        TurnListener listener = new GraphicTurnListener(this, goban);
        goban.setGameBoard(new GameBoard(new HumanPlayer("Player 1", listener), new HumanPlayer("Player 2", listener), goban.getSize()));
        goban.startGame();
    }
}
