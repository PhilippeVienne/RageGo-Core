package com.ragego.network;

import com.ragego.engine.GameBoard;
import com.ragego.engine.GameNode;
import com.ragego.engine.HumanPlayer;
import us.monoid.json.JSONArray;
import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;
import us.monoid.web.JSONResource;
import us.monoid.web.Resty;

import java.io.IOException;
import java.util.*;

/**
 * Server class to communicate with the game server.
 */
@SuppressWarnings("UseSparseArrays")
public class RageGoServer extends Resty {

    private static final String RAGEGO_SERVER = "http://ragego-server.herokuapp.com";
    private static final List<NewGameListener> newGameListeners = Collections.synchronizedList(new ArrayList<NewGameListener>(1));
    private static RageGoServer instance;
    private static Map<Integer, OnlineGame> games = Collections.synchronizedMap(new HashMap<Integer, OnlineGame>());
    private static Map<Integer, OnlinePlayer> players = Collections.synchronizedMap(new HashMap<Integer, OnlinePlayer>());
    private static Map<Integer, OnlineNode> nodes = Collections.synchronizedMap(new HashMap<Integer, OnlineNode>());
    private static OnlinePlayer localPlayer;
    private static boolean listeningNewGameThread = false;
    private static Thread listenNewGameThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (listeningNewGameThread) {
                try {
                    JSONArray games = getInstance().json(RAGEGO_SERVER + "/games/for/" + String.valueOf(RageGoServer.getLocalPlayerID()) + ".json").array();
                    for (int i = 0; i < games.length(); i++) {
                        final OnlineGame game = OnlineGame.loadFromJSON(games.getJSONObject(i));
                        for (NewGameListener listener : newGameListeners) {
                            listener.newGame(game);
                        }
                    }
                } catch (IOException e) {
                    throw handleException(e);
                } catch (JSONException e) {
                    throw handleException(e);
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    listeningNewGameThread = false;
                }
            }
        }
    }, "NewGameThread");

    public static RageGoServer getInstance(){
        if(instance == null)
            instance = new RageGoServer();
        return instance;
    }

    public static OnlinePlayer getPlayer(String code) throws RageGoServerException {
        try {
            final OnlinePlayer onlinePlayer = OnlinePlayer.loadFromJSON(getInstance().json(RAGEGO_SERVER + "/player/" + code + ".json").object());
            if (onlinePlayer != null && players.containsKey(onlinePlayer.getId())) {
                return players.get(onlinePlayer.getId());
            }
            if (onlinePlayer != null)
                players.put(onlinePlayer.getId(), onlinePlayer);
            return onlinePlayer;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    public static OnlinePlayer getPlayer(int id) throws RageGoServerException {
        if (players.containsKey(id)) {
            return players.get(id);
        }
        try {
            final OnlinePlayer onlinePlayer = OnlinePlayer.loadFromJSON(getPlayerJSONObject(id));
            players.put(id, onlinePlayer);
            return onlinePlayer;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    private static JSONObject getPlayerJSONObject(int id) throws IOException, JSONException {
        return getInstance().json(getPlayerURL(id)).object();
    }

    private static String getPlayerURL(int id) {
        return RAGEGO_SERVER + "/players/" + String.valueOf(id) + ".json";
    }

    public static OnlinePlayer createPlayer(boolean playing) throws RageGoServerException{
        try{
            final JSONResource resource = getInstance().json(getPlayersURL(), form(data("player[playing]", playing ? "1" : "0")));
            final OnlinePlayer onlinePlayer = OnlinePlayer.loadFromJSON(resource.object());
            players.put(onlinePlayer.getId(), onlinePlayer);
            return onlinePlayer;
        } catch (Exception e){
            throw handleException(e);
        }
    }

    private static String getPlayersURL() {
        return RAGEGO_SERVER + "/players.json";
    }

    public static void deletePlayer(OnlinePlayer player) throws RageGoServerException{
        try {
            getInstance().json(getPlayerURL(player.getId()), delete());
            if (players.containsValue(player)) {
                players.remove(player.getId());
            }
        } catch (IOException e) {
            throw handleException(e);
        }
    }

    public static OnlineGame getGame(int id) throws RageGoServerException{
        if (games.get(id) != null)
            return games.get(id);
        try {
            final OnlineGame onlineGame = OnlineGame.loadFromJSON(getGameJSONObject(id));
            games.put(id, onlineGame);
            return onlineGame;
        } catch(Exception e){
            throw handleException(e);
        }
    }

    private static JSONObject getGameJSONObject(int id) throws IOException, JSONException {
        return getInstance().json(getGameURL(id)).object();
    }

    private static String getGameURL(int id) {
        return RAGEGO_SERVER + "/games/" + String.valueOf(id) + ".json";
    }

    public static OnlineGame createGame(OnlinePlayer blacks, OnlinePlayer whites) throws RageGoServerException{
        try {
            final JSONResource resource = getInstance().json(getGamesURL(),
                    form(
                            data("game[whites_id]", String.valueOf(whites.getId())),
                            data("game[blacks_id]", String.valueOf(blacks.getId()))
                    ));
            final OnlineGame onlineGame = new OnlineGame(resource.object().getInt("id"), whites, blacks);
            games.put(onlineGame.getId(), onlineGame);
            return onlineGame;
        } catch (Exception e){
            throw handleException(e);
        }
    }

    private static String getGamesURL() {
        return RAGEGO_SERVER + "/games.json";
    }

    public static OnlineNode getNode(int id, GameBoard board) throws RageGoServerException {
        if (nodes.containsKey(id))
            return nodes.get(id);
        try {
            final OnlineNode onlineNode = OnlineNode.loadFromJSON(getInstance().json(RAGEGO_SERVER + "/nodes/" + String.valueOf(id) + ".json").object(), board);
            nodes.put(id, onlineNode);
            return onlineNode;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    public static OnlineGame join(OnlineGame game) {
        try {
            getInstance().json(RAGEGO_SERVER + "/games/" + String.valueOf(game.getId()) + "/join.json");
        } catch (IOException e) {
            throw handleException(e);
        }
        return game;
    }

    public static OnlineNode createNode(GameNode node, OnlineGame game, OnlinePlayer player) throws RageGoServerException {
        try {
            final OnlineNode onlineNode = OnlineNode.loadFromJSON(getInstance().json(RAGEGO_SERVER + "/nodes.json", form(
                    data("node[player_id]", String.valueOf(player.getId())),
                    data("node[game_id]", String.valueOf(game.getId())),
                    data("node[data]", GameNode.serialize(node))
            )).object(), node.getBoard());
            nodes.put(onlineNode.getId(), onlineNode);
            return onlineNode;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    public static void purgeCache() {
        games.clear();
        players.clear();
        nodes.clear();
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

    public static ArrayList<Integer> getNodesFor(OnlineGame game, OnlinePlayer player) throws RageGoServerException {
        ArrayList<Integer> ids = new ArrayList<Integer>();
        try {
            final JSONArray nodes = getInstance().json(RAGEGO_SERVER + "/nodes/for/game/" + String.valueOf(game.getId()) + "/player/" + String.valueOf(player.getId()) + ".json").array();
            for (int i = 0; i < nodes.length(); i++) {
                ids.add(nodes.getJSONObject(i).getInt("id"));
            }
        } catch (Exception e) {
            throw handleException(e);
        }
        return ids;
    }

    public static void addListener(NewGameListener listener) {
        newGameListeners.add(listener);
    }

    public static void removeListener(NewGameListener listener) {
        synchronized (newGameListeners) {
            try {
                newGameListeners.wait();
                newGameListeners.remove(listener);
            } catch (InterruptedException e) {
                newGameListeners.remove(listener);
            }
        }
    }

    public static void startWaitingForGame() {
        if (listeningNewGameThread) return;
        listeningNewGameThread = true;
        listenNewGameThread.start();
    }

    public static void stopWaitingForGame() {
        listeningNewGameThread = false;
    }

    public static int getLocalPlayerID() {
        return localPlayer.getId();
    }

    public static OnlinePlayer getLocalPlayer() {
        return localPlayer;
    }

    public static OnlinePlayer updateLocalPlayer(HumanPlayer player) {
        localPlayer = createPlayer(false);
        localPlayer.setListener(player.getListener());
        return localPlayer;
    }

    public interface NewGameListener {
        void newGame(OnlineGame game);
    }
}
