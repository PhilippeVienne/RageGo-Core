package com.ragego.engine;

import com.ragego.engine.GameNode;
import com.ragego.engine.Player;

import java.util.LinkedList;

/**
 * Test player to simulate players on GameBoard.
 * This are simple players where you can register all turns to act.
 */
public class TestPlayer extends Player {

    public LinkedList<GameNode> nodesToPlay = new LinkedList<GameNode>();

    public TestPlayer() {
        listener = new TestPlayerTurnListener(this);
    }

    public void registerNodeToPlay(GameNode node) {
        nodesToPlay.add(node);
    }

    public GameNode getNextNode() {
        return nodesToPlay.pop();
    }

    public boolean hasNextNode() {
        return 0 != nodesToPlay.size();
    }

    @Override
    public String getDisplayName() {
        return "TestPlayer" + hashCode();
    }

    /**
     * Create a node to play at given coordinates.
     * @param coordinates {@link Intersection#get(String, GameBoard)}
     */
    public void registerNodeToPlay(GameBoard board, String... coordinates) {
        for (String coordinate : coordinates) {
            registerNodeToPlay(new GameNode(board, null, GameNode.Action.PUT_STONE, Intersection.get(coordinate, board), this));
        }
    }
}