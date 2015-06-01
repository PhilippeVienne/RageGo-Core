package com.ragego.engine;

import com.ragego.utils.DebugUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Represent a board of Go.
 * <p>
 * On this board, intersections are represented with numerical coordinates :
 * <ul>
 * <li>The first coordinate is the column from left to right.</li>
 * <li>The second coordinate is the line from top to bottom.</li>
 * </ul>
 * <p>Each intersection is assigned to a shape, empty (or assigned to something special)</p>
 * <p>First player has black color and the second is white.</p>
 *
 * @author Philippe Vienne
 */
public class GameBoard {

    /**
     * Default size for a Go board.
     */
    public static final int DEFAULT_BOARD_SIZE = 19;
    public static boolean DEBUG_MODE = false;
    private final Player firstPlayer;
    private final Player secondPlayer;
    /**
     * Listeners for this board.
     * All this listeners are called when some actions occurs. Do not forget to warn them.
     * This data should not be copied between games.
     */
    private final ArrayList<GameListener> listeners = new ArrayList<GameListener>(10);
    /**
     * Score counter for the current game.
     * This data should not be copied between games.
     */
    private final ScoreCounter scoreCounter;
    private boolean gameEnded = false;
    /**
     * Register the last validated node in the game.
     */
    private GameNode lastNode;
    /**
     * This should be {@link #secondPlayer} or {@link #firstPlayer}
     */
    private Player currentPlayer;
    private int boardSize = DEFAULT_BOARD_SIZE;
    /**
     * Store the elements on bord by {@link com.ragego.engine.Intersection}
     */
    private HashMap<Intersection, Stone> board;
    /**
     * Declare if IA functions are callable.
     */
    private boolean ia_functions_enabled = false;
    private ArrayList<Intersection> boardIntersections;
    private GameNode rootNode;

    /**
     * Create a board with the default size.
     *
     * @param firstPlayer  The first player (conventional black stone player)
     * @param secondPlayer The second player (conventional white stone player)
     */
    public GameBoard(Player firstPlayer, Player secondPlayer) {
        this(firstPlayer, secondPlayer, DEFAULT_BOARD_SIZE);
    }

    /**
     * Create a board with a custom size.
     *
     * @param firstPlayer  The first player (conventional black stone player)
     * @param secondPlayer The second player (conventional white stone player)
     * @param boardSize    Number of column for this board
     */
    public GameBoard(Player firstPlayer, Player secondPlayer, int boardSize) {
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
        this.boardSize = boardSize;
        this.board = new HashMap<Intersection, Stone>(this.boardSize * this.boardSize);
        lastNode = new GameNode(this, GameNode.Action.START_GAME);
        rootNode = lastNode;
        this.scoreCounter = new ScoreCounter(this);
    }

    /**
     * Play a new move.
     * Apply the Rule 7 to make a new move.
     */
    public void nextMove() {
        { // Update the player
            if (lastNode == null) currentPlayer = getFirstPlayer();
            else currentPlayer = getOpponent(lastNode.getPlayer());
            if (currentPlayer == null) currentPlayer = getFirstPlayer();
        }
        { // Spread events that a turn is starting
            final String signature = getBoardHash();
            getFirstPlayer().getListener().startOfTurn(this, currentPlayer, getOpponent(currentPlayer));
            getSecondPlayer().getListener().startOfTurn(this, currentPlayer, getOpponent(currentPlayer));
            for (GameListener listener : listeners) {
                listener.startOfTurn(this, currentPlayer, getOpponent(currentPlayer));
            }
            if (!signature.equals(getBoardHash())) {
                throw new IllegalStateException("A player has modified the board, and this should not be");
            }
        }
        // Play the turn
        currentPlayer.getListener().newTurn(this, currentPlayer);
        for (GameListener listener : listeners) {
            listener.newTurn(this, currentPlayer);
        }
        if (!lastNode.isLocked()) {
            lastNode.recomputeHash();
            lastNode.lock();
        }
        { // Spread events that a turn is ended
            final String signature = getBoardHash();
            getFirstPlayer().getListener().endOfTurn(this, currentPlayer, getOpponent(currentPlayer));
            getSecondPlayer().getListener().endOfTurn(this, currentPlayer, getOpponent(currentPlayer));
            for (GameListener listener : listeners) {
                listener.endOfTurn(this, currentPlayer, getOpponent(currentPlayer));
            }
            if (!signature.equals(getBoardHash())) {
                throw new IllegalStateException("A player has modified the board, and this should not be");
            }
        }
    }

    /**
     * Check that we can play on this intersection.
     *
     * @param node The {@link GameNode} which represent what you want to play.
     * @throws GoRuleViolation If rules are not respected.
     * @return true if it's correct following the Go rules to play on this row.
     */
    public boolean canPlay(GameNode node) throws GoRuleViolation {

        if (node.getAction() != GameNode.Action.PUT_STONE) { // Only evaluate rules on put stone action
            return true;
        }

        // Fake board
        GameBoard testBoard = copyBoard();

        // Result storage
        GoRuleViolation.Type type = null;
        boolean isViolatingRule = false;
        String message = null;

        // Retrive useful data from the node
        GameNode testNode = node.copy(testBoard);

        { // Check we are playing on the current board
            checkBoardForIntersection(node.getIntersection());
        }
        { // Rule 7 :  Placing a stone of their color on an empty intersection.
            if (isNotEmpty(node.getIntersection())) return false;
        }
        { // Rule # : You can ot kill you
            testBoard.actNode(testNode);
            ArrayList<Stone> deadStones = testNode.getDeadStones();
            if (deadStones.contains(testNode.getStone())) {
                message = "You can not kill yourself";
                type = GoRuleViolation.Type.SUICIDE;
                isViolatingRule = true;
            }
        }
        if (!isViolatingRule) { // Rule 8 : A play may not recreate a previous position from the game.
            if (testNode.isMakingKO()) {
                DebugUtils.printBoard(testBoard);
                type = GoRuleViolation.Type.KO;
                message = "You made a position that exists";
                isViolatingRule = true;
            }
        }
        // No inspection due to a false inspection result.
        //noinspection ConstantConditions
        if (message != null && isViolatingRule) {
            throw new GoRuleViolation(type, message);
        }
        // No rule violation
        return true;
    }

    /**
     * Play a node on this board.
     * This method act a node on this board.<br>
     * Actions performed: <ol>
     * <li>Assigns this board to the node</li>
     * <li>Checks if action is not violating Go rules (throws {@link IllegalArgumentException} if not).</li>
     * <li>Sets {@link GameBoard#lastNode} as parent</li>
     * <li>Prevents listeners</li>
     * <li>Act the action</li>
     * <li>Recompute hash for the node and lock the node</li>
     * </ol>
     *
     * @param node The action to play
     */
    public void play(GameNode node) {
        node.setBoard(this);
        try {
            if (!canPlay(node)) {
                throw new IllegalArgumentException("The wanted action is violating a Go rule");
            }
        } catch (GoRuleViolation goRuleViolation) {
            throw new IllegalArgumentException("The wanted action is violating a Go rule", goRuleViolation);
        }
        actNode(node);
        if (DEBUG_MODE) {
            System.out.println("Played node: " + node);
            System.out.println("Board hash is = " + getBoardHash());
            DebugUtils.printBoard(this);
        }
    }

    /**
     * Act a node on the board.
     * This function does what was written for {@link #play(GameNode)} but does not check rules.
     *
     * @param node The node to play
     */
    private void actNode(GameNode node) {
        node.setParent(lastNode);
        if (currentPlayer != null) // We are not in an auto-computing mode.
            node.setPlayer(currentPlayer);
        lastNode = node;
        for (GameListener listener : listeners) {
            listener.playNode(node);
        }
        switch (node.getAction()) {
            case START_GAME:
                board = new HashMap<Intersection, Stone>(boardSize * boardSize);
                loadBoardFromArray(node.getRawData());
                break;
            case PASS:
                if (node.getParent().getAction() == GameNode.Action.PASS) {
                    gameEnded = true;
                    scoreCounter.getScore();
                }
                break;
            case PUT_STONE:
                placeStoneOnBoard(node.getStone());
                node.getDeadStones().addAll(computeDeadStone(getOpponent(node.getPlayer())));
                node.getDeadStones().addAll(computeDeadStone(node.getPlayer()));
                break;
            case IA_SPECIAL_ACTION:
                if (node.getRawData() != null) {
                    board = new HashMap<Intersection, Stone>(boardSize * boardSize);
                    loadBoardFromArray(node.getRawData());
                    break;
                }
                try {
                    IAPlayer iaPlayer = (IAPlayer) currentPlayer;
                    ia_functions_enabled = true;
                    if (iaPlayer != null)
                        iaPlayer.makeSpecialTurn();
                    ia_functions_enabled = false;
                } catch (ClassCastException e) {
                    throw new RuntimeException("Player is not an IAPlayer");
                }
                break;
            case NOTHING:
                break;
            default:
                throw new IllegalArgumentException("Node is not valid");
        }
        lastNode.recomputeHash();
        lastNode.lock();
    }

    /**
     * Load a game from an array.
     * Create stone for each not null cell in table
     *
     * @param data Raw data of this board.
     *             <p>0 is "No stone here", 1 is "Stone owned by Black (or first player)" and
     *             2 is "Stone owned by White (or second player)". First index is line, second is column.
     */
    private void loadBoardFromArray(int[][] data) {
        if(data == null) return;
        for (int line = 0; line < data.length; line++) {
            for (int column = 0; column < data[line].length; column++) {
                if (data[line][column] != 1 && data[line][column] != 2) continue;
                placeStoneOnBoard(
                        new Stone(
                                Intersection.get(column, line, this),
                                (
                                        data[line][column] == 1 ?
                                                getBlackPlayer() :
                                                getWhitePlayer()
                                )
                        )
                );
            }
        }
    }

    /**
     * Get a player symbole for int representation
     *
     * @param player The player who we want to get sign
     * @return The number to use
     */
    private int getPlayerSign(Player player) {
        if (player == null)
            return 0;
        else if (player == firstPlayer)
            return 1;
        else if (player == secondPlayer)
            return 2;
        else
            throw new IllegalArgumentException("Not a player on this board.");
    }

    /**
     * Retrieve the size of this board.
     *
     * @return The number of column and lines on board
     */
    public int getBoardSize() {
        return boardSize;
    }

    public void setBoardSize(int boardSize) {
        this.boardSize = boardSize;
    }

    /**
     * Retrieve something on Intersection
     *
     * @param intersection The intersection where the element should be
     * @return The element or null if it's empty
     */
    public Stone getElement(Intersection intersection) {
        checkBoardForIntersection(intersection);
        return board.get(intersection);
    }

    /**
     * Check that intersection is on the current board.
     *
     * @param intersection The intersection to check
     */
    private void checkBoardForIntersection(Intersection intersection) {
        if (intersection.getBoard() != this) {
            System.err.println("Intersection is on board " + intersection.getBoard());
            System.err.println("and you are on board " + this);
            // This is really a bad patch :
            // throw new IllegalStateException("Assertion false, you are not on goot board");
        }
    }

    /**
     * Check and remove dead stones.
     *
     * @param player The player who we want to remove stones
     * @return The dead stones
     */
    public ArrayList<Stone> computeDeadStone(Player player) {
        ArrayList<Stone> deadStones = new ArrayList<Stone>();

        // Look for dead stones.
        // On each shape, check if it's alive.
        final ArrayList<StoneGroup> stoneGroups = getStoneGroups();
        for (StoneGroup stoneGroup : stoneGroups) {
            if (stoneGroup != null && stoneGroup.getPlayer() == player && !stoneGroup.isAlive()) {
                for (Stone deadStone : new ArrayList<Stone>(stoneGroup.getStones())) {
                    deadStones.add(deadStone);
                    removeStoneFromBoard(deadStone);
                }
            }
        }

        // Return set of dead stones
        return deadStones;
    }

    /**
     * Do necessary calls to register a stone as dead.
     *
     * @param stone        The stone to kill
     */
    private void removeStoneFromBoard(Stone stone) {
        stone.setCaptivated();
        board.remove(stone.getPosition());
        if (stone.getStoneGroup() != null)
            stone.getStoneGroup().removeStone(stone);
        for (GameListener listener : listeners) {
            listener.stoneRemoved(stone);
        }
    }

    /**
     * Put an stone to an intersection.
     * This function don't check that the stone checks go rules.
     *
     * @param stone      The stone to put
     */
    private void placeStoneOnBoard(Stone stone) {
        final Intersection intersection = stone.getPosition();
        checkBoardForIntersection(intersection);
        stone.setCaptivated(false);
        StoneGroup stoneGroup = searchForStoneGroupAround(intersection, stone.getPlayer());
        if (stoneGroup == null)
            stoneGroup = new StoneGroup(stone.getPlayer(), this, stone);
        else
            stoneGroup.addStone(stone);
        stone.setStoneGroup(stoneGroup);
        board.put(intersection, stone);
        for (GameListener listener : listeners) {
            listener.newStoneAdded(stone);
        }
    }

    /**
     * Lookup for a shape which can be connected to an intersection.
     * If there is multiple stoneGroups which can be connected, return a fusion of
     * them.
     *
     * @param intersection The intersection where we lookup
     * @param player       The player which should be the owner of shape
     * @return The shape to associate with or null if there is no one.
     */
    private StoneGroup searchForStoneGroupAround(Intersection intersection, Player player) {
        ArrayList<StoneGroup> stoneGroups = new ArrayList<StoneGroup>(4);
        for (Intersection neighbours : intersection.getNeighboursIntersections()) {
            if (getElement(neighbours) != null && getElement(neighbours).getPlayer() == player) {
                StoneGroup stoneGroup = getElement(neighbours).getStoneGroup();
                if (stoneGroup == null) {
                    stoneGroup = new StoneGroup(player, this, getElement(neighbours));
                }
                if (!stoneGroups.contains(stoneGroup))
                    stoneGroups.add(stoneGroup);
            }
        }
        Iterator<StoneGroup> shapeIterator = stoneGroups.iterator();
        switch (stoneGroups.size()) {
            case 0:
                return null;
            case 1:
                return shapeIterator.next();
            case 2:
            case 3:
            case 4:
                StoneGroup newStoneGroup = shapeIterator.next();
                for (StoneGroup stoneGroup : stoneGroups) {
                    if (stoneGroup != null)
                        newStoneGroup.unionWith(stoneGroup);
                }
                return newStoneGroup;
        }
        return null; // We never never should go here
    }

    /**
     * Retrieve the white player
     *
     * @return The player or null if it's empty
     */
    public Player getFirstPlayer() {
        return firstPlayer;
    }

    /**
     * Retrieve the black player.
     *
     * @return The player or null if it's empty
     */
    public Player getSecondPlayer() {
        return secondPlayer;
    }

    /**
     * Create a primitive-type representation of the game.
     * First index is for line, second is for column
     *
     * @return A table where : 0 is an empty row, 1 for first player, 2 for second
     */
    public int[][] getRepresentation() {
        int[][] data = new int[boardSize][boardSize];
        for (Intersection intersection : board.keySet()) {
            final Stone stone = board.get(intersection);
            if(isValidIntersection(intersection))
            data[intersection.getLine()][intersection.getColumn()] = getPlayerSign(stone.getPlayer());
        }
        return data;
    }

    /**
     * Get the other player that one.
     * This function is useful when you use the opponent
     *
     * @param player The player for who we look for opponent
     * @return The opponent player (null if there is not)
     */
    public Player getOpponent(Player player) {
        if (player == getFirstPlayer()) return getSecondPlayer();
        if (player == getSecondPlayer()) return getFirstPlayer();
        return null;
    }

    /**
     * Check if intersection is empty.
     *
     * @param intersection The intersection to check
     * @return true if no stones are on this intersection
     */
    public boolean isEmpty(Intersection intersection) {
        return !isNotEmpty(intersection);
    }

    /**
     * Check if intersection is not empty.
     *
     * @param intersection The intersection to check
     * @return false if no stones are on this intersection
     */
    public boolean isNotEmpty(Intersection intersection) {
        return board.containsKey(intersection);
    }

    /**
     * Check if intersection is on board
     *
     * @param intersection The intersection to test
     * @return true if it's on board.
     */
    public boolean isValidIntersection(Intersection intersection) {
        return intersection.getColumn() >= 0 && intersection.getColumn() < boardSize && intersection.getLine() >= 0 && intersection.getLine() < boardSize;
    }

    /**
     * Get a string of the board state.
     * Compute a string which is unique in function of board stones positions.
     * @return A unique hash for the current board situation
     */
    public String getBoardHash() {
        StringBuilder message = new StringBuilder();
        int[][] data = getRepresentation();
        for (int[] ints : data) {
            for (int val : ints) {
                message.append(val);
            }
        }
        try {
            return md5(message.toString()).toUpperCase();
        } catch (Exception e) {
            return message.toString(); // A bigger hash
        }
    }

    /**
     * A "simple" code to make a MD5 strings on all Java Systems.
     * Note if you try another method, please test android because there will be some errors.
     * @param s The string to hash
     * @return The MD5 string of the params.
     */
    public final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return s;
    }

    /**
     * Retrieve Shapes from this board.
     * @return array of stoneGroups
     */
    public ArrayList<StoneGroup> getStoneGroups() {
        ArrayList<StoneGroup> stoneGroups = new ArrayList<StoneGroup>();
        for (Stone stone : board.values()) {
            if (!stoneGroups.contains(stone.getStoneGroup())) {
                stoneGroups.add(stone.getStoneGroup());
            }
        }
        return stoneGroups;
    }

    /**
     * Get the black player.
     * In Go rules, the black player start the game so this function is an easy way to access to first player.
     *
     * @return The first player
     * @see #getFirstPlayer()
     */
    public Player getBlackPlayer() {
        return getFirstPlayer();
    }

    /**
     * Get the white player.
     * In Go rules, the white player do not start the game so this function is an easy way to access to second player.
     *
     * @return The second player
     * @see #getSecondPlayer()
     */
    public Player getWhitePlayer() {
        return getSecondPlayer();
    }

    /**
     * Get the last played node on this board. Notice if this is equal with {@link #getRootNode()}, it means the game
     * is simply empty.
     * @return The last node for this board.
     */
    public GameNode getLastNode() {
        return lastNode;
    }

    /**
     * Create a complete copy of this GameBoard.
     * A copy is a heavy action because it will copy all stoneGroups and stones (do it with cautious).<br>
     * Data which is copied :
     * <ul>
     * <li>StoneGroups: see {@link StoneGroup#copy(GameBoard)}</li>
     * <li>Stones: see {@link Stone#copy(GameBoard)}</li>
     * <li>Nodes: see {@link GameNode#copy(GameBoard)}</li>
     * </ul>
     * Listeners are not copied to not call on fake events (but you can copy them).
     *
     * @return A board with copied data.
     */
    public GameBoard copyBoard() {

        GameBoard board = new GameBoard(firstPlayer, secondPlayer);

        board.boardSize = boardSize;

        final ArrayList<StoneGroup> stoneGroups = getStoneGroups();
        for (StoneGroup stoneGroup : stoneGroups) {
            for (Stone stone : stoneGroup.copy(board).getStones())
                board.board.put(stone.getPosition(), stone);
        }

        GameNode node = lastNode, newNode = lastNode.copy(board);

        board.lastNode = newNode;

        while (node.hasParent()) {
            node = node.getParent();
            newNode.setParent(node.copy(board));
            newNode = newNode.getParent();
        }

        return board;
    }

    /**
     * Get ScoreCounter attached to this board
     * @return The ScoreCounter
     */
    public ScoreCounter getScoreCounter() {
        return scoreCounter;
    }

    /**
     * Add a listener to this board.
     *
     * @param listener the listener to add to this {@link GameBoard}
     * @see GameListener
     */
    public void addGameListener(GameListener listener) {
        listeners.add(listener);
    }

    /**
     * Remove a listener from this board.
     *
     * @param listener the listener to remove.
     */
    @SuppressWarnings("unused")
    public void removeGameListener(GameListener listener) {
        listeners.remove(listener);
    }

    //////////////////////////////////////////////////////////:
    ////        Functions for IA

    /**
     * Get letter that represent for player.
     * W is for White player and B is for Black player.
     *
     * @param player The player to get the letter
     * @return The letter capitalized
     */
    public char getLetterForPlayer(Player player) {
        return getBlackPlayer() == player ? 'B' : (getWhitePlayer() == player ? 'W' : '?');
    }

    /**
     * Get the board.
     * @return The hash map currently in use by this board to store stones
     */
    public HashMap<Intersection, Stone> ia_getBoard() {
        checkIAMode();
        return board;
    }

    /**
     * Create a stone and compute dead stones.
     * Put a stone on intersection and compute dead stones.
     * If there is already a stone, it deletes it and creates a new one.
     *
     * @param intersection Where to place the stone.
     * @param player       The player who owns the stone.
     * @return The new stone
     */
    public Stone ia_createStoneAndComputeDead(Intersection intersection, Player player) {
        Stone stone = ia_createStone(intersection, player);
        computeDeadStone(getOpponent(player));
        computeDeadStone(player);
        return stone;
    }

    /**
     * Create a stone.
     * Put a stone on intersection and wait end of turn before compute dead stones.
     * If there is already a stone, it deletes it and creates a new one.
     *
     * @param intersection Where to place the stone.
     * @param player       The player who owns the stone.
     * @return The new stone
     */
    public Stone ia_createStone(Intersection intersection, Player player) {
        checkIAMode();
        if (getElement(intersection) != null) ia_deleteStone(getElement(intersection));
        Stone stone = new Stone(intersection, player);
        placeStoneOnBoard(stone);
        return stone;
    }

    /**
     * Delete a stone.
     * Delete a stone from the board without any side effects.
     *
     * @param stone The stone to delete.
     */
    public void ia_deleteStone(Stone stone) {
        checkIAMode();
        removeStoneFromBoard(stone);
    }

    /**
     * Move a stone.
     * This move a stone to a new location. If there is already a stone, it does nothing.
     *
     * @param stone        The stone to move
     * @param intersection The new intersection
     * @return The new Stone moved to the position or the old if it was not possible
     */
    public Stone ia_moveStone(Stone stone, Intersection intersection) {
        checkIAMode();
        checkBoardForIntersection(intersection);
        if (getElement(intersection) != null) return stone;
        Stone newStone = new Stone(intersection, stone.getPlayer());
        removeStoneFromBoard(stone);
        placeStoneOnBoard(newStone);
        return newStone;
    }

    /**
     * Check if this board has entered in IA mode.
     * If not, it should throws an exception.
     * @throws IllegalStateException If we are not in IA mode.
     */
    private void checkIAMode() {
        if (!ia_functions_enabled)
            throw new IllegalStateException("You can not call IA functions here !");
    }

    /**
     * Get a stone at given coordinates.
     * @param column The column between 0 and boardSize - 1
     * @param line The line between 0 and boardSize - 1
     * @return The stone on this coordinates or just null if there is not.
     */
    public Stone getElement(int column, int line) {
        return getElement(Intersection.get(column, line, this));
    }

    /**
     * Find the player on a given intersection.
     * @param p The intersection you are looking at.
     * @return The player on this intersection or null if it's empty.
     */
    public Player getPlayerOn(Intersection p) {
        final Stone element = getElement(p);
        return element == null ? null : element.getPlayer();
    }

    /**
     * Generate an array of all intersections of this board.
     * @return The intersections of this board.
     */
    public ArrayList<Intersection> getBoardIntersections() {
        ArrayList<Intersection> intersections = new ArrayList<Intersection>(boardSize * boardSize);
        for (int x = 0; x < boardSize; x++)
            for (int y = 0; y < boardSize; y++) {
                intersections.add(Intersection.get(x, y, this));
            }
        return intersections;
    }

    /**
     * Get the id used by the game to designate this player.
     * @param player The player you want to determine the index
     * @return 1 for first player, 2 for second player and 0 in all other cases.
     */
    public int getNumberForPlayer(Player player) {
        return player == firstPlayer ? 1 : (player == secondPlayer ? 2 : 0);
    }

    /**
     * Get the player which is playing.
     * This function is only valid during the execution of {@link #nextMove()} function (on another thread for sample).
     * @return The player who must play.
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Get all stones on this board
     * @return An array of stones presents on this board.
     */
    public ArrayList<? extends Stone> getStones() {
        return new ArrayList<Stone>(board.values());
    }

    /**
     * Remove the last node played on this game.
     * <p>For that, it remove the stone added, replace dead stones on the board and correct the score</p>
     */
    public void removeLastNode() {
        GameNode canceledNode = lastNode;
        currentPlayer = null;
        if (canceledNode.getParent() == null) return; // Not cancelable
        lastNode = canceledNode.getParent();
        if (canceledNode.getAction() == GameNode.Action.PUT_STONE) { // We must recompute game
            scoreCounter.setCountingDeadStones(false);
            removeStoneFromBoard(canceledNode.getStone());
            for (Stone s : canceledNode.getDeadStones()) {
                placeStoneOnBoard(s);
            }
            scoreCounter.cancelNode(canceledNode);
            if (gameEnded) {
                gameEnded = false;
            }
            scoreCounter.setCountingDeadStones(true);
        }
        currentPlayer = lastNode.getPlayer();
    }

    /**
     * Forward in the node tree.
     * It will remake a node if the current node has a child node.
     * The game supposed the child is valid for Go Rules.
     */
    public void remakeNode() {
        if (!lastNode.hasChild()) return;
        currentPlayer = null;
        actNode(lastNode.getChild());
        currentPlayer = getOpponent(lastNode.getPlayer());
    }

    /**
     * Get the main node of this game.
     * @return This node has no parent and marks the start of the game.
     */
    public GameNode getRootNode() {
        return rootNode;
    }

    /**
     * Determine if the game is ended.
     * Go is ended if the two players has passed their turns.
     * @return true if you should consider this game as ended.
     */
    public boolean isGameEnded() {
        return gameEnded;
    }
}
