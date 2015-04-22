package com.ragego.network;

import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;

/**
 * Represents a game on the server
 */
public class OnlineGame {

    private int id;
    private OnlinePlayer whites;
    private OnlinePlayer blacks;

    public OnlineGame(int id, OnlinePlayer whites, OnlinePlayer blacks) {
        this.id = id;
        this.whites = whites;
        this.blacks = blacks;
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

    public static OnlineGame loadFromJSON(JSONObject object) throws JSONException {
        return new OnlineGame(
                object.getInt("id"),
                RageGoServer.getPlayer(object.getInt("whites_id")),
                RageGoServer.getPlayer(object.getInt("blacks_id"))
        );
    }
}
