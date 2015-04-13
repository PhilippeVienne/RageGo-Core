package com.ragego.engine;

import java.util.Stack;

/**
 * This class compute a game for a given node.
 * This class is useful to recompute a game when it has been saved. This follow a GameNode to recompute a game state.
 *
 * @see GameNode
 */
public class GameComputer {

    private final GameBoard board;

    /**
     * Contains node that should be played.
     *
     * @see Stack
     */
    private Stack<GameNode> nodesToPlay;

    /**
     * Create a GameComputer for a given board and game
     *
     * @param endNode Final node. This is the wanted state.
     * @param board   A GameBoard. This is used to load layers on new GameBoard.
     */
    public GameComputer(GameNode endNode, GameBoard board) {
        this.board = new GameBoard(board.getFirstPlayer(), board.getSecondPlayer());
        nodesToPlay = new Stack<GameNode>();
        GameNode node = endNode;
        while (node != null) {
            nodesToPlay.push(node);
            node = node.getParent();
        }
    }

    /**
     * Apply the nodes to the GameBoard.
     * You should call it before trying call {@link #getBoard()}
     *
     * @param ignoreRuleViolation Ignore a node if violate go rules.
     */
    public void compute(boolean ignoreRuleViolation) {
        while (!nodesToPlay.empty()) {
            final GameNode gameNode = nodesToPlay.pop();
            final Intersection intersection = gameNode.getIntersection();
            if (intersection != null) // Update board if necessary
                gameNode.setIntersection(Intersection.get(intersection.getColumn(), intersection.getLine(), board));
            try {
                board.play(gameNode);
            } catch (IllegalArgumentException error) {
                if (!ignoreRuleViolation)
                    throw error;
            }
        }
    }

    /**
     * Get the new GameBoard
     *
     * @return The board where we apply nodes.
     */
    public GameBoard getBoard() {
        return board;
    }

}
