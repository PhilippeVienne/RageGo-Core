package com.ragego.network;

import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;
import us.monoid.web.JSONResource;
import us.monoid.web.Resty;

import java.io.IOException;

/**
 * Server class to communicate with the game server.
 */
public class RageGoServer extends Resty {

    private static RageGoServer instance;

    public static RageGoServer getInstance(){
        if(instance == null)
            instance = new RageGoServer();
        return instance;
    }

    public static OnlinePlayer getPlayer(int id) throws IOException, JSONException {
        return OnlinePlayer.loadFromJSON(getInstance().json("http://ragego-server.herokuapp.com/player/"+String.valueOf(id)+".json"));
    }

    public static int createUserOnline() throws IOException, JSONException {
        final JSONResource resource = getInstance().json("http://ragego-server.herokuapp.com/players.json", form(data("player", data("playing", String.valueOf(0)))));
        return resource.object().getInt("id");
    }
}
