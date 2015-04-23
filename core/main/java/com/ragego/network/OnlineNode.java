package com.ragego.network;

import com.ragego.engine.GameBoard;
import com.ragego.engine.GameNode;
import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;

/**
 * Represents a node played online.
 */
public class OnlineNode {

    public static final String PLAYER_ID_KEY = "player_id";
    public static final String GAME_ID_KEY = "game_id";
    public static final String ID_KEY = "id";
    public static final String DATA_KEY = "data";
    private GameNode node;
    private int id;
    private OnlinePlayer player;
    private OnlineGame game;

    protected OnlineNode(int id, int player_id, int game_id, String serializedData, GameNode node) {
        game = RageGoServer.getGame(game_id);
        player = RageGoServer.getPlayer(player_id);
        this.id = id;
        this.node = GameNode.unserialize(node, serializedData);
    }

    protected OnlineNode(int id, int player_id, int game_id, String serializedData, GameBoard board) {
        this(id, player_id, game_id, serializedData, new GameNode(board, GameNode.Action.NOTHING));
    }

    /**
     * Restore an OnlineNode from a server JSONObject.
     *
     * @param object The object from the server.
     * @param board  The board where this node should be played. It only use it to set the board for {@link GameNode}
     * @return The OnlineNode which represent this GameNode.
     * @throws JSONException If the data from server does not contain required information.
     */
    public static OnlineNode loadFromJSON(JSONObject object, GameBoard board) throws JSONException {
        final int player_id = object.getInt(PLAYER_ID_KEY),
                game_id = object.getInt(GAME_ID_KEY),
                id = object.getInt(ID_KEY);
        final String data = object.getString(DATA_KEY);
        return new OnlineNode(id, player_id, game_id, data, board);
    }

    public GameNode getNode() {
        return node;
    }

    public String getSerializedData() {
        return GameNode.serialize(node);
    }

    public int getId() {
        return id;
    }

    public OnlinePlayer getPlayer() {
        return player;
    }

    public OnlineGame getGame() {
        return game;
    }
}
