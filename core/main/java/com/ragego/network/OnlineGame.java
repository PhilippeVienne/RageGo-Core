package com.ragego.network;

import com.ragego.engine.GameBoard;
import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;

import java.util.ArrayList;

/**
 * Represents a game on the server
 */
public class OnlineGame {

    private int id;
    private OnlinePlayer whites;
    private OnlinePlayer blacks;
    private ArrayList<Integer> playedNode = new ArrayList<Integer>();

    public OnlineGame(int id, OnlinePlayer whites, OnlinePlayer blacks) {
        this.id = id;
        this.whites = whites;
        this.blacks = blacks;
    }

    public static OnlineGame loadFromJSON(JSONObject object) throws JSONException {
        return new OnlineGame(
                object.getInt("id"),
                RageGoServer.getPlayer(object.getInt("whites_id")),
                RageGoServer.getPlayer(object.getInt("blacks_id"))
        );
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public OnlinePlayer getWhites() {
        return whites;
    }

    public void setWhites(OnlinePlayer whites) {
        this.whites = whites;
    }

    public OnlinePlayer getBlacks() {
        return blacks;
    }

    public void setBlacks(OnlinePlayer blacks) {
        this.blacks = blacks;
    }

    public OnlineNode waitForNewNode(OnlinePlayer player, GameBoard board) {
        OnlineNode node = null;
        do {
            final ArrayList<Integer> nodes = RageGoServer.getNodesFor(this, player);
            nodes.removeAll(playedNode);
            if (nodes.size() == 1) {
                int id = nodes.get(0);
                node = RageGoServer.getNode(id, board);
            } else if (nodes.size() > 1) {
                throw new RageGoServerException(RageGoServerException.ExceptionType.UNKNOWN, null);
            } else {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                }
            }
        } while (node == null);
        playedNode.add(node.getId());
        return node;
    }
}
