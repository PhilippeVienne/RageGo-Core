package com.ragego.engine;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.Callable;

/**
 * Represent a board of Go.
 *
 * On this board, intersections are represented with numerical coordinates :
 * <ul>
 *  <li>The first coordinate is the column from left to right.</li>
 *  <li>The second coordinate is the line from top to bottom.</li>
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

    /**
     * Register the last validated node in the game.
     */
    private GameNode lastNode;

    private Player firstPlayer;
    private Player secondPlayer;
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
     * Declare if we want to delete the dead stones.
     * This is useful to not delete dead stones in rule test mode.
     */
    private boolean is_deleting_dead_stones = true;

    /**
     * Declare if IA functions are callable.
     */
    private boolean ia_functions_enabled = false;

    /**
     * Create an real empty board.
     * This constructor is suitable only for test purpose. 
     */
    public GameBoard() {
        this(null, null,DEFAULT_BOARD_SIZE);
    }

    /**
     * Create a board with the default size. 
     * @param firstPlayer The first player (conventional white stone player)
     * @param secondPlayer The second player (conventional black stone player)
     */
    public GameBoard(Player firstPlayer, Player secondPlayer) {
        this(firstPlayer, secondPlayer, DEFAULT_BOARD_SIZE);
    }

    /**
     * Create a board with a custom size.
     * @param firstPlayer The first player (conventional white stone player)
     * @param secondPlayer The second player (conventional black stone player)
     * @param boardSize Number of column for this board
     */
    public GameBoard(Player firstPlayer, Player secondPlayer, int boardSize) {
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
        this.boardSize = boardSize;
        this.board = new HashMap<>(this.boardSize*this.boardSize);
        lastNode=new GameNode(this, GameNode.Action.START_GAME);
    }

    /**
     * Play a new move.
     * Apply the Rule 7 to make a new move.
     */
    public void nextMove(){
        Player previousPlayer;
        { // Update the player
            previousPlayer = currentPlayer == null ? getSecondPlayer() : currentPlayer;
            currentPlayer = currentPlayer == getFirstPlayer() ? getSecondPlayer() : getFirstPlayer();
        }
        { // Spread events that a turn is starting
                doNotEditBoard(()->{
                    getFirstPlayer().getListener().startOfTurn(this, currentPlayer, previousPlayer);
                    getSecondPlayer().getListener().startOfTurn(this, currentPlayer, previousPlayer);
                    return null;
                });
        }
        is_deleting_dead_stones = false;
        // Play the turn
        currentPlayer.getListener().newTurn(this,currentPlayer);
        // Compute the dead stones
        is_deleting_dead_stones = true;
        computeDeadStone(previousPlayer);
        computeDeadStone(currentPlayer);
        lastNode.recomputeHash();
        lastNode.lock();
        { // Spread events that a turn is ended
            doNotEditBoard(() -> {
                getFirstPlayer().getListener().endOfTurn(this, currentPlayer, previousPlayer);
                getSecondPlayer().getListener().endOfTurn(this, currentPlayer, previousPlayer);
                return null;
            });
        }
    }

    private void doNotEditBoard(Callable<Void> callable) {
        final String signature = getBoardHash();
        try {
            callable.call();
        } catch (Exception e) {
            throw new RuntimeException("Error during event handling",e);
        }
        if (!signature.equals(getBoardHash())) {
            throw new IllegalStateException("A player has modified the board, and this should not be");
        }
    }

    /**
     * Check that we can play on this intersection.
     * @param node The {@link GameNode} which represent what you want to play.
     * @return true if it's correct following the Go rules to play on this row.
     */
    public boolean canPlay(GameNode node) throws GoRuleViolation{
        // Save old board to restore after use
        HashMap<Intersection,Stone> oldBoard = new HashMap<>(board);

        // Result storage
        GoRuleViolation.Type type = null;
        boolean isViolatingRule = false;
        String message = null;

        // Register the node as child of current (temporary)
        lastNode.addChild(node);

        // Retrive useful data from the node
        Intersection intersection=node.getIntersection();
        Player player=node.getPlayer();
        Stone testStone = new Stone(intersection,player);
        { // Check we are playing on the current board
            checkBoardForIntersection(intersection);
        }
        { // Rule 7 :  Placing a stone of their color on an empty intersection.
            if(isNotEmpty(intersection)) return false;
        }
        { // Rule # : You can ot kill you
            setElement(intersection,testStone);
            computeDeadStone(getOpponent(player));
            Stone[] deadStone = computeDeadStone(player);
            for(Stone stone:deadStone)
                if(stone == testStone){
                    message = "You can not kill yourself";
                    type = GoRuleViolation.Type.SUCIDE;
                    isViolatingRule = true;
                }
        }
        board = new HashMap<>(oldBoard); // Reset the computing
        if(!isViolatingRule){ // Rule 8 : A play may not recreate a previous position from the game.
            setElement(intersection, testStone);
            computeDeadStone(getOpponent(player));
            computeDeadStone(player);
            node.recomputeHash();
            if(node.isMakingKO()){
                type = GoRuleViolation.Type.KO;
                message = "You made a position that exists";
                isViolatingRule = true;
            }
        }
        board = oldBoard;
        lastNode.removeChild(node);
        // No inspection due to a false inspection result.
        //noinspection ConstantConditions
        if(message!=null&&isViolatingRule){
            throw new GoRuleViolation(type,message);
        }
        // No rule violation
        return true;
    }

    public void play(GameNode node){
        try {
            if(!canPlay(node)){
                throw new IllegalArgumentException("The wanted action is violating a Go rule");
            }
        } catch (GoRuleViolation goRuleViolation) {
            throw new IllegalArgumentException("The wanted action is violating a Go rule",goRuleViolation);
        }
        node.setParent(lastNode);
        lastNode = node;
        switch (node.getAction()){

            case START_GAME:
                break;
            case PASS:
                break;
            case PUT_STONE:
                setElement(node.getIntersection(),node.getStone());
                break;
            case IA_SPECIAL_ACTION:
                try {
                    IAPlayer iaPlayer = (IAPlayer) currentPlayer;
                    ia_functions_enabled = true;
                    iaPlayer.makeSpecialTurn();
                    ia_functions_enabled = false;
                } catch (ClassCastException e) {
                    throw new RuntimeException("Player is not an IAPlayer");
                }
                break;
        }
    }

    /**
     * Get a player symbole for int representation
     * @param player The player who we want to get sign
     * @return The number to use
     */
    private int getPlayerSign(Player player) {
        if(player == null)
            return 0;
        else if(player == firstPlayer)
            return 1;
        else if(player == secondPlayer)
            return 2;
        else
            throw new IllegalArgumentException("Not a player on this board.");
    }

    /**
     * Retrieve the size of this board.
     * @return The number of column and lines on board
     */
    public int getBoardSize() {
        return boardSize;
    }

    /**
     * Retrieve something on Intersection
     *
     * @param intersection The intersection where the element should be
     * @return The element or null if it's empty
     */
    public Stone getElement(Intersection intersection) {
        checkBoardForIntersection(intersection);
        return board.getOrDefault(intersection, null);
    }

    /**
     * Check that intersection is on the current board.
     * @param intersection The intersection to check
     * @throws com.ragego.engine.GameBoard.BadBoardException If it's not the good board
     */
    private void checkBoardForIntersection(Intersection intersection) throws BadBoardException{
        if (intersection.getBoard() != this)
            throw new BadBoardException();
    }

    /**
     * Check and remove dead stones.
     * @return The dead stones
     * @param player The player who we want to remove stones
     */
    public Stone[] computeDeadStone(Player player){
        HashMap<Intersection,Stone> deadStones = new HashMap<>();

        // Look for dead stones.
        // On each shape, check if it's alive.
        board.forEach((intersection,stone)->{
            if(stone!=null&&stone.getPlayer() == player&&stone.getShape()!=null&&!stone.getShape().isAlive())
                stone.getShape().getStones().forEach((deadStone)-> deadStones.put(deadStone.getPosition(),deadStone));
        });

        // If we are in test mode we only delete stones from the board but we wont they know they are deleted
        if (is_deleting_dead_stones) deadStones.forEach(this::deadStone);
        else deadStones.forEach(board::remove);

        // Return set of dead stones
        return deadStones.values().toArray(new Stone[deadStones.size()]);
    }

    /**
     * Do necessary calls to register a stone as dead.
     * @param intersection Intersection where the stone was
     * @param stone The stone to kill
     */
    private void deadStone(Intersection intersection, Stone stone) {
        stone.setCaptivated();
        board.remove(intersection,stone);
    }

    /**
     * Put an element to an intersection.
     * This function don't check that the element checks go rules. 
     * @param intersection One of intersection of this board
     * @param element The element to put
     */
    private void setElement(Intersection intersection, Stone element){
        checkBoardForIntersection(intersection);
        Shape shape = searchForShapesAround(intersection, element.getPlayer());
        if(shape == null)
            shape = new Shape(element.getPlayer(),this,element);
        else
            shape.addStone(element);
        element.setShape(shape);
        board.put(intersection, element);
    }

    /**
     * Lookup for a shape which can be connected to an intersection.
     * If there is multiple shapes which can be connected, return a fusion of
     * them.
     * @param intersection The intersection where we lookup
     * @param player The player which should be the owner of shape
     * @return The shape to associate with or null if there is not one.
     */
    private Shape searchForShapesAround(Intersection intersection, Player player) {
        ArrayList<Shape> shapes = new ArrayList<>(4);
        for (Intersection neighbours : intersection.getNeighboursIntersections()) {
            if(getElement(neighbours) != null && getElement(neighbours).getPlayer() == player) {
                Shape shape = getElement(neighbours).getShape();
                if(shape == null)
                    throw new IllegalStateException("A stone have no associated shape.");
                shapes.add(shape);
            }
        }
        Iterator<Shape> shapeIterator = shapes.iterator();
        switch (shapes.size()){
            case 0:
                return null;
            case 1:
                return shapeIterator.next();
            case 2:
            case 3:
            case 4:
                Shape newShape = shapeIterator.next();
                shapeIterator.forEachRemaining(newShape::unionWith);
                return  newShape;
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
     * @return A table where : 0 is an empty row, 1 for first player, 2 for second
     */
    public int[][] getRepresentation(){
        int[][] data = new int[boardSize][boardSize];
        board.forEach((intersection, goElement) -> data[intersection.getLine()][intersection.getColumn()] =
        getPlayerSign(goElement.getPlayer()));
        return data;
    }

    /**
     * Get the other player that one.
     * This function is useful when you use the opponent
     *
     * @param player The player for who we look for opponent
     * @return The opponent player (null if there is not)
     */
    private Player getOpponent(Player player){
        if(player == getFirstPlayer()) return getSecondPlayer();
        if (player == getSecondPlayer()) return getFirstPlayer();
        return null;
    }

    /**
     * Check if intersection is empty.
     * @param intersection The intersection to check
     * @return true if no stones are on this intersection
     */
    public boolean isEmpty(Intersection intersection) {
        return !board.containsKey(intersection);
    }

    /**
     * Check if intersection is not empty.
     * @param intersection The intersection to check
     * @return false if no stones are on this intersection
     */
    public boolean isNotEmpty(Intersection intersection) {
        return !isEmpty(intersection);
    }

    /**
     * Check if intersection is on board
     * @param intersection The intersection to test
     * @return true if it's on board.
     */
    public boolean isValidIntersection(Intersection intersection) {
        return intersection.getColumn()>=0&&intersection.getColumn()<boardSize&&intersection.getLine()>=0&&intersection.getLine()<boardSize;
    }

    /**
     * Get a string of the board state.
     * Compute a string which is unique in function of board stones positions.
     */
    public String getBoardHash(){
        String digest = "error";
        int[][] data = getRepresentation();
        byte[] bytes = new byte[data.length*data[0].length];
        for (int i1 = 0, dataLength = data.length; i1 < dataLength; i1++) {
            int[] line = data[i1];
            for (int i2 = 0, columnLength = line.length; i2 < columnLength; i2++) {
                int i = line[i2];
                bytes[i1*dataLength+i2] = (byte) i;
            }
        }
        try {
            digest = new String(MessageDigest.getInstance("MD5").digest(bytes));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return digest;
    }

    /**
     * Get the black player.
     * In Go rules, the black player start the game so this function is an easy way to access to first player.
     * @return The first player
     * @see #getFirstPlayer()
     */
    public Player getBlackPlayer(){
        return getFirstPlayer();
    }

    /**
     * Get the white player.
     * In Go rules, the white player do not start the game so this function is an easy way to access to second player.
     * @return The second player
     * @see #getSecondPlayer()
     */
    public Player getWhitePlayer(){
        return getSecondPlayer();
    }

    /**
     * Get letter that represent for player.
     * W is for White player and B is for Black player.
     * @return The letter capitalized
     */
    public char getLetterForPlayer(Player player){
        return getBlackPlayer() == player?'B':(getWhitePlayer()==player?'W':'?');
    }

    //////////////////////////////////////////////////////////:
    ////        Functions for IA

    /**
     * Get the board.
     */
    public HashMap<Intersection,Stone> ia_getBoard(){
        checkIAMode();
        return board;
    }

    /**
     * Create a stone and compute dead stones.
     * Put a stone on intersection and compute dead stones.
     * If there is already a stone, it deletes it and creates a new one.
     * @param intersection Where to place the stone.
     * @param player The player who owns the stone.
     * @return The new stone
     */
    public Stone ia_createStoneAndComputeDead(Intersection intersection, Player player){
        Stone stone = ia_createStone(intersection, player);
        computeDeadStone(getOpponent(player));
        computeDeadStone(player);
        return stone;
    }

    /**
     * Create a stone.
     * Put a stone on intersection and wait end of turn before compute dead stones.
     * If there is already a stone, it deletes it and creates a new one.
     * @param intersection Where to place the stone.
     * @param player The player who owns the stone.
     * @return The new stone
     */
    public Stone ia_createStone(Intersection intersection, Player player){
        checkIAMode();
        if(getElement(intersection)!=null) ia_deleteStone(getElement(intersection));
        Stone stone = new Stone(intersection,player);
        setElement(intersection, stone);
        return stone;
    }

    /**
     * Delete a stone.
     * Delete a stone from the board whithout any side effects.
     * @param stone The stone to delete.
     */
    public void ia_deleteStone(Stone stone){
        checkIAMode();
        deadStone(stone.getPosition(), stone);
    }

    /**
     * Move a stone.
     * This move a stone to a new location. If there is already a stone, it does nothing.
     * @param stone The stone to move
     * @param intersection The new intersection
     * @return The new Stone moved to the position or the old if it was not possible
     */
    public Stone ia_moveStone(Stone stone, Intersection intersection){
        checkIAMode();
        checkBoardForIntersection(intersection);
        if(getElement(intersection)!=null)return stone;
        Stone newStone = new Stone(intersection,stone.getPlayer());
        deadStone(stone.getPosition(), stone);
        setElement(intersection, newStone);
        return newStone;
    }

    private void checkIAMode() {
        if(!ia_functions_enabled)
            throw new IllegalStateException("You can not call IA functions here !");
    }

    public GameNode getLastNode() {
        return lastNode;
    }

    /**
     * Exception occurs when you are manipulating a wrong board.
     */
    public static class BadBoardException extends IllegalArgumentException{

        public BadBoardException() {
            super("Not on good board");
        }
    }
}
