package com.ragego.gui.objects;

import com.ragego.engine.*;

/**
 * Listener for GameBoard for a given Goban.
 * All graphics stuff should be done here.
 */
public class GobanGameBoardListener implements GameListener {

    final private Goban goban;

    public GobanGameBoardListener(Goban goban) {
        this.goban = goban;
    }

    @Override
    public void newStoneAdded(Stone stone) {
        goban.addGraphicStone(new GraphicStone(stone, goban));
    }

    @Override
    public void stoneRemoved(Stone stone) {
        goban.removeGraphicStone(goban.getGraphicStone(stone));
    }

    @Override
    public void playNode(GameNode node) {
        goban.animate(node);
    }

    @Override
    public void newTurn(GameBoard board, Player player) {

    }

    @Override
    public void endOfTurn(GameBoard board, Player player, Player nextPlayer) {
        goban.updateCurrentPlayer();
        goban.refreshUserScore();
    }

    @Override
    public void startOfTurn(GameBoard board, Player player, Player previousPlayer) {
        goban.updateCurrentPlayer();
    }
}
