package com.ragego.network;

import com.ragego.engine.HumanPlayer;
import com.ragego.engine.TurnListener;
import com.ragego.gui.RageGoGame;
import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;
import us.monoid.web.JSONResource;

import java.io.IOException;

/**
 * Created by Philippe Vienne on 21/04/2015.
 */
public class OnlinePlayer extends HumanPlayer{

    private final int id;

    /**
     * Create a new local online player
     *
     * @param name     His name (e.g.: Joe Doe)
     * @param listener Listener for this player
     */
    public OnlinePlayer(String name, TurnListener listener) throws IOException, JSONException {
        super(name, listener);
        id=createUserOnline();
    }

    private OnlinePlayer(int id, String code) {
        super(code, new OnlinePlayerListener());
        this.id = id;
    }

    private int createUserOnline() throws IOException, JSONException {
        return RageGoServer.createUserOnline();
    }

    /**
     * Create an online player from server data
     * @param json Server data for this Player
     * @throws JSONException if server data is incorrect
     * @throws IOException if server data is incorrect
     */
    public static OnlinePlayer loadFromJSON(JSONResource json) throws IOException, JSONException {
        final JSONObject object = json.toObject();
        int id = object.getInt("id");
        final String code = object.get("code").toString();
        return new OnlinePlayer(id,code);
    }
}
