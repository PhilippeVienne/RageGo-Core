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

    private OnlinePlayer(int id, String code) {
        super(code, new OnlinePlayerListener());
        this.id = id;
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

    public int getId() {
        return id;
    }
}
