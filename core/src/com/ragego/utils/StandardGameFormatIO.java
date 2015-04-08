package com.ragego.utils;

import com.ragego.engine.GameNode;
import com.ragego.engine.GameBoard;

import java.io.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Vector;

/**
 * This class read an SGF file.
 * SGF is a standard format to store Game data and is mostly used for Go games.
 * @see com.ragego.utils.FormatIO For more details about behavior of this class
 * @see <a href="http://senseis.xmp.net/?SmartGameFormat">SGF for Go details</a>
 */
@SuppressWarnings("UnusedDeclaration") // Because it's an API so all functions are not used.
public class StandardGameFormatIO implements FormatIO {

    private File file;
    private GameBoard game;

    /**
     * @see #StandardGameFormatIO(java.io.File, com.ragego.engine.GameBoard)
     */
    public StandardGameFormatIO() {
        this(null,null);
    }

    /**
     * @see #StandardGameFormatIO(java.io.File, com.ragego.engine.GameBoard)
     * @param game See {@link #StandardGameFormatIO(java.io.File, com.ragego.engine.GameBoard)}
     */
    public StandardGameFormatIO(GameBoard game) {
        this(null, game);
    }

    /**
     * @see #StandardGameFormatIO(java.io.File, com.ragego.engine.GameBoard)
     * @param file See {@link #StandardGameFormatIO(java.io.File, com.ragego.engine.GameBoard)}
     */
    public StandardGameFormatIO(File file) {
        this(file, null);
    }

    /**
     * Construct a SGF Reader/Writer.
     * @param file The file which will be read and/or write.
     * @param game The game to update date in.
     */
    public StandardGameFormatIO(File file, GameBoard game) {
        this.file = file;
        this.game = game;
    }

    @Override
    public boolean write() throws IOException {
        return false;
    }

    @Override
    public boolean write(GameBoard game) throws IOException {
        return false;
    }

    @Override
    public boolean write(File file, GameBoard game) throws IOException {
        return false;
    }

    @Override
    public GameNode[] readRaw() throws IOException {
        return new GameNode[0];
    }

    @Override
    public GameNode[] readRaw(File file) throws IOException {
        return new GameNode[0];
    }

    /**
     * Tokenizer to simple read sgf file.
     * This is useful to not rewrite a tokenizer :-D.
     */
    private StreamTokenizer tokenizer;

    @Override
    public boolean read() throws IOException {
        return read(file,game);
    }

    @Override
    public boolean read(GameBoard game) throws IOException {
        return read(file, game);
    }

    @Override
    public boolean read(File file, GameBoard game) throws IOException {
        if(this.game != game){
            throw new IllegalArgumentException("You can not read to a different game instance than the FormatIO instance");
        }
        this.game = game; // As this, it's fixed.

        // Open and setup the tokenizer
        tokenizer = new StreamTokenizer(new FileReader(file));
        tokenizer.slashSlashComments(false);
        tokenizer.slashStarComments(false);

        // Look to start of game data
        while (true) { // Not infinite, exit on '(' found or throw error on EOF
            int ttype = tokenizer.nextToken();
            if (ttype == StreamTokenizer.TT_EOF)
                throw sgfError("No game tree found!");

            if (ttype == '(') {
                break;
            }
        }

        // Now we can start to read, we call recursive
        Node root = readNode(null, true);

        // Fill the game with node data
        fillGameWithNode(root);

        // If we come here, all happened correctly
        return false;
    }

    /**
     * Fill the game with root node data (and child)
     * @param root Main node in the game
     */
    private void fillGameWithNode(Node root) {

    }

    /**
     * Create an IOError from SGF
     * @param s The description of SGF error.
     * @return The IOException (nothing extraordinary)
     */
    private IOException sgfError(String s) {
        return new IOException("[SGF Error] " + s);
    }

    /**
     * Read a node from the tokenizer.
     * TODO:Explain this function
     * @param parent The parent node
     * @param is_root Define if we are on root node (only one time in a parse)
     * @return The completed node with game data. On root, return the main game node.
     */
    private Node readNode(Node parent, boolean is_root) {
        if(is_root && parent == null){ // Create the root node
            parent = new Node();
        }

        return parent;
    }

    /**
     * Represent a node in SGF Format.
     */
    private static class Node {

        private static final String W = "W";
        private static final String B = "B";
        private static final String AB = "AB";
        private static final String AW = "AW";
        private Node parent;
        private Vector<Node> children = new Vector<>();
        private HashMap<String,String> data = new HashMap<>();

        public Node(){}

        public Node(String data){
            parseData(data);
        }

        public Node getParent() {
            return parent;
        }

        public void setParent(Node parent) {
            this.parent = parent;
            if(!parent.getChildren().contains(this))
                parent.addChild(this);
        }

        public Vector<Node> getChildren() {
            return children;
        }

        public void addChild(Node child) {
            this.children.add(child);
        }

        public String get(String property){
            return data.containsKey(property)?data.get(property):null;
        }

        public String set(String property, String data){
            return this.data.put(property,data);
        }

        public boolean isWhiteMove(){
            return data.containsKey(W);
        }

        public boolean isBlackMove(){
            return data.containsKey(B);
        }

        public int[] getMoveCoordinates(){
            if(!isBlackMove()&&!isWhiteMove()) return null;
            String move = isWhiteMove()?get(W):get(B);
            if(move.length()!=2&&move.matches("[A-Sa-s]{2}"))
                throw new IllegalStateException("A move contains 2 coordinates within [a-s] or [A-S]");
            move = move.toLowerCase(Locale.US);
            return new int[]{(int)(move.toCharArray()[0])-96,(int)(move.toCharArray()[1])-96};
        }

        public void parseData(String data){
            boolean isReadingKey = true;
            boolean isReadingData = false;
            boolean isReadingOpenSign = false;
            boolean isReadingCloseSign = false;
            StringBuilder bufferKey=new StringBuilder();
            StringBuilder bufferData = new StringBuilder();
            for(char c:data.toCharArray()){
                if(c=='['&&isReadingKey){
                    isReadingKey = false;
                    isReadingOpenSign = true;
                }else if(c==']'&&isReadingData){
                    isReadingData = false;
                    isReadingCloseSign = true;
                    this.data.put(bufferKey.toString(),bufferData.toString());
                    bufferData = new StringBuilder();
                    bufferKey = new StringBuilder();
                }
                if(Character.isLetterOrDigit(c)){
                    if(isReadingOpenSign){
                        isReadingOpenSign = false;
                        isReadingData = true;
                    } else if(isReadingCloseSign){
                        isReadingCloseSign = false;
                        isReadingKey = true;
                    }
                }
                if(isReadingKey){
                    bufferKey.append(c);
                } else if(isReadingData){
                    bufferData.append(c);
                }
            }
        }

    }
}
