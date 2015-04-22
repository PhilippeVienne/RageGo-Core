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

    public static OnlinePlayer getPlayer(int id) throws RageGoServerException {
        try {
            return OnlinePlayer.loadFromJSON(getInstance().json("http://ragego-server.herokuapp.com/players/" + String.valueOf(id) + ".json"));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    public static OnlinePlayer createPlayer(boolean playing) throws RageGoServerException{
        try{
            final JSONResource resource = getInstance().json("http://ragego-server.herokuapp.com/players.json", form(data("player[playing]", playing?"1":"0")));
            return OnlinePlayer.loadFromJSON(resource);
        } catch (Exception e){
            throw handleException(e);
        }
    }

    public static void deletePlayer(OnlinePlayer player) throws RageGoServerException{
        try {
            getInstance().json("http://ragego-server.herokuapp.com/players/"+String.valueOf(player.getId())+".json",delete());
        } catch (IOException e) {
            throw handleException(e);
        }
    }

    public static OnlineGame getGame(int id) throws RageGoServerException{
        try {
            return OnlineGame.loadFromJSON(getInstance().json("http://ragego-server.herokuapp.com/games/" + String.valueOf(id) + ".json").object());
        } catch(Exception e){
            throw handleException(e);
        }
    }

    public static OnlineGame createGame(OnlinePlayer blacks, OnlinePlayer whites) throws RageGoServerException{
        try {
            final JSONResource resource = getInstance().json("http://ragego-server.herokuapp.com/games.json",
                    form(
                            data("game[whites_id]", String.valueOf(whites.getId())),
                            data("game[blacks_id]", String.valueOf(blacks.getId()))
                    ));
            return new OnlineGame(resource.object().getInt("id"), blacks, whites);
        } catch (Exception e){
            throw handleException(e);
        }
    }

    public static RageGoServerException handleException(Exception e) {
        if(e instanceof IOException){
            return new RageGoServerException(RageGoServerException.ExceptionType.OFFLINE, e);
        }
        if(e instanceof JSONException){
            return new RageGoServerException(RageGoServerException.ExceptionType.DATA_MALFORMED, e);
        }
        return new RageGoServerException(RageGoServerException.ExceptionType.UNKNOWN, e);
    }
}
