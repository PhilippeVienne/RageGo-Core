package com.ragego.network;

import com.ragego.engine.HumanPlayer;
import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;

import java.io.IOException;

public class OnlinePlayer extends HumanPlayer{

    private final int id;
    private final String code;
    private OnlineGame currentGame;

    private OnlinePlayer(int id, String code) {
        super(code, new OnlinePlayerListener());
        this.id = id;
        this.code = code;
    }

    /**
     * Create an online player from server data
     * @param object Server data for this Player
     * @throws JSONException if server data is incorrect
     * @throws IOException if server data is incorrect
     * @return The player corresponding
     */
    public static OnlinePlayer loadFromJSON(JSONObject object) throws IOException, JSONException {
        if (object == null) return null;
        int id = object.getInt("id");
        final String code = object.get("code").toString();
        return new OnlinePlayer(id,code);
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public OnlineGame getCurrentGame() {
        return currentGame;
    }

    public void setCurrentGame(OnlineGame currentGame) {
        this.currentGame = currentGame;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof OnlinePlayer) {
            return ((OnlinePlayer) obj).id == id;
        }
        return super.equals(obj);
    }
}
