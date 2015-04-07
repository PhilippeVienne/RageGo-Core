package com.ragego.utils;

import com.ragego.engine.BoardSnap;
import com.ragego.engine.GameBoard;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
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
    public BoardSnap[] readRaw() throws IOException {
        return new BoardSnap[0];
    }

    @Override
    public BoardSnap[] readRaw(File file) throws IOException {
        return new BoardSnap[0];
    }

    @Override
    public boolean read() throws IOException {
        return read(file,game);
    }

    @Override
    public boolean read(GameBoard game) throws IOException {
        return read(file,game);
    }

    @Override
    public boolean read(File file, GameBoard game) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        StringBuilder builder = new StringBuilder();
        Files.readAllLines(file.toPath()).forEach(builder::append);
        String data = builder.toString();
        readMainNode(data,game);
        return false;
    }

    private void readMainNode(String data, GameBoard game) {

    }

    private Node readNode(Node parent, String data, GameBoard game) {
        StringBuilder builder = new StringBuilder();
        char[] datas = data.toCharArray();
        boolean isReadingNode = false;
        boolean isReadingOpenMultipleChild = false;
        boolean isReadingCloseChild = false;
        boolean isReadingRoof = false;
        int position = 0;
        if(datas.length<1)return;
        if(datas[0] == ';'){
            position++;
        }
        isReadingNode = true;
        do{
            if(!isReadingRoof&&isReadingNode&&datas[position]==';'&&((datas[position-1]!='\\')||(datas[position-2]!='\\'&&datas[position-1]!='\\'))){
                Node node = new Node(builder.toString());
                builder = new StringBuilder();
            }
            else if(isReadingNode&&!isReadingRoof){} // TODO Continue to work on this
        } while (position!=datas.length);
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

        private void parseData(String data){
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
