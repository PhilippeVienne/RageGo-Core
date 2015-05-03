package com.ragego.gui;

import com.badlogic.gdx.math.Vector2;
import com.ragego.engine.*;
import com.ragego.gui.objects.Goban;
import com.ragego.gui.screens.GoGameScreen;

/**
 * Turn listener for a graphical environment.
 */
public class GraphicTurnListener implements TurnListener {

    private final GoGameScreen screen;
    private final Goban goban;

    /**
     * Create a turn listener with a screen and a goban
     *
     * @param screen The {@link GoGameScreen} displayed on screen.
     */
    public GraphicTurnListener(GoGameScreen screen) {
        this(screen, null);
    }

    /**
     * Create a turn listener with a screen and a goban
     *
     * @param screen The {@link GoGameScreen} displayed on screen.
     * @param goban  The goban where this listener act.
     */
    public GraphicTurnListener(GoGameScreen screen, Goban goban) {
        this.screen = screen;
        this.goban = goban;
    }

    @Override
    public void newTurn(GameBoard board, Player player) {
        Intersection intersection = Intersection.get(-1, -1, board);
        GameNode node = new GameNode(board, GameNode.Action.NOTHING);
        boolean canPlay = false;
        do {
            final Vector2 input = goban.waitForUserInputOnGoban();
            if (input == null) {
                node = new GameNode(board, null, GameNode.Action.PASS, intersection, player);
            } else {
                intersection = Intersection.get((int) input.y, (int) input.x, goban.getBoard());
                if (!goban.getBoard().isValidIntersection(intersection)) continue;
                node = new GameNode(goban.getBoard(), null, GameNode.Action.PUT_STONE, intersection, player);
            }
            try {
                canPlay = goban.getBoard().canPlay(node);
            } catch (GoRuleViolation goRuleViolation) {
                System.out.print("Rule violation : " + goRuleViolation.getMessage());
                canPlay = false;
            }
        }
        while ((!(goban.getBoard().isValidIntersection(intersection) || node.getAction() == GameNode.Action.PASS) && canPlay));

        // Apply turn on game
        if (node.getAction() == GameNode.Action.PASS || node.getAction() == GameNode.Action.PUT_STONE) {
            goban.getBoard().play(node);
        }
    }

    @Override
    public void endOfTurn(GameBoard board, Player player, Player nextPlayer) {
    }

    @Override
    public void startOfTurn(GameBoard board, Player player, Player previousPlayer) {

    }
}
