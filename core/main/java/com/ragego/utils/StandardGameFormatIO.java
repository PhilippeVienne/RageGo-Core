package com.ragego.utils;

import com.ragego.engine.GameBoard;
import com.ragego.engine.GameComputer;
import com.ragego.engine.GameNode;
import com.ragego.engine.Intersection;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;

/**
 * This class read an SGF file.
 * SGF is a standard format to store Game data and is mostly used for Go games.
 *
 * @see com.ragego.utils.FormatIO For more details about behavior of this class
 * @see <a href="http://senseis.xmp.net/?SmartGameFormat">SGF for Go details</a>
 */
@SuppressWarnings("UnusedDeclaration") // Because it's an API so all functions are not used.
public class StandardGameFormatIO implements FormatIO {

    private static final int GM_CODE = 111;
    GameNode rootNode;
    GameNode currentNode;
    private File file;
    private GameBoard game;
    private FileReader reader;
    /**
     * Tokenizer to simple read sgf file.
     * This is useful to not rewrite a tokenizer :-D.
     */
    private StreamTokenizer tokenizer;

    /**
     * @see #StandardGameFormatIO(java.io.File, com.ragego.engine.GameBoard)
     */
    public StandardGameFormatIO() {
        this(null, null);
    }

    /**
     * @param game See {@link #StandardGameFormatIO(java.io.File, com.ragego.engine.GameBoard)}
     * @see #StandardGameFormatIO(java.io.File, com.ragego.engine.GameBoard)
     */
    public StandardGameFormatIO(GameBoard game) {
        this(null, game);
    }

    /**
     * @param file See {@link #StandardGameFormatIO(java.io.File, com.ragego.engine.GameBoard)}
     * @see #StandardGameFormatIO(java.io.File, com.ragego.engine.GameBoard)
     */
    public StandardGameFormatIO(File file) {
        this(file, null);
    }

    /**
     * Construct a SGF Reader/Writer.
     *
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

    @Override
    public GameBoard read() throws IOException {
        return read(file, game);
    }

    @Override
    public GameBoard read(GameBoard game) throws IOException {
        return read(file, game);
    }

    @Override
    public GameBoard read(File file, GameBoard game) throws IOException {
        if (this.game != game) {
            throw new IllegalArgumentException("You can not read to a different game instance than the FormatIO instance");
        }
        this.game = game; // As this, it's fixed.
        this.rootNode = null; // Delete previous data.
        this.currentNode = null;

        // Open and setup the tokenizer
        reader = new FileReader(file);
        tokenizer = new StreamTokenizer(reader);
        tokenizer.slashSlashComments(false);
        tokenizer.slashStarComments(false);

        // Look to start of game data
        while (true) { // Not infinite, exit on '(' found or throw error on EOF
            int ttype = tokenizer.nextToken();
            if (ttype == StreamTokenizer.TT_EOF)
                throw sgfError("No game tree found!");

            if (ttype == '(') {
                tokenizer.pushBack();
                break;
            }
        }

        // Now we can start to read, we call recursive
        parseBranch(null, true);

        // Apply Game to board
        GameComputer computer = new GameComputer(currentNode, game);
        computer.compute(false);
        this.game = computer.getBoard();

        // If we come here, all happened correctly
        return this.game;
    }

    /**
     * Create an IOError from SGF
     *
     * @param s The description of SGF error.
     * @return The IOException (nothing extraordinary)
     */
    private IOException sgfError(String s) {
        return new IOException("[SGF Error] " + s);
    }

    /**
     * Read a node from the tokenizer.
     * TODO:Explain this function
     *
     * @param parent  The parent node
     * @param is_root Define if we are on root node (only one time in a parse)
     * @return The completed node with game data. On root, return the main game node.
     */
    private GameNode parseNode(GameNode parent, boolean is_root) throws IOException {

        int ttype = tokenizer.nextToken();
        if (ttype != ';')
            throw sgfError("Error at head of node!");

        boolean next_is_root = is_root;

        // Create this node
        GameNode node = new GameNode(game, parent, GameNode.Action.NOTHING);
        currentNode = node;
        if (parent != null)
            parent.addChild(node);
        if (is_root) { // Create the root node
            node.setAction(GameNode.Action.START_GAME);
            rootNode = node;
        }

        boolean done = false;
        while (!done) {
            ttype = tokenizer.nextToken();
            switch (ttype) {
                case '(':
                    tokenizer.pushBack();
                    parseBranch(node, false);
                    break;

                case ';':
                    tokenizer.pushBack();
                    parseNode(next_is_root ? null : node, next_is_root);
                    done = true;
                    break;

                case ')':
                    tokenizer.pushBack();
                    done = true;
                    break;

                case StreamTokenizer.TT_WORD:
                    if (is_root) {
                        next_is_root = false;
                    }
                    parseProperty(node, is_root);
                    break;

                case StreamTokenizer.TT_EOF:
                    throw sgfError("Unexpected EOF in node!");

                default:
                    throw sgfError("Error in SGF file.");
            }
        }

        return parent;
    }

    private void parseProperty(GameNode node, boolean is_root) throws IOException {
        int x, y;
        String name = tokenizer.sval;

        boolean done = false;
        while (!done) {

            int ttype = tokenizer.nextToken();
            if (ttype != '[')
                done = true;
            tokenizer.pushBack();
            if (!done) {
                String val;
                if (name.equals("C"))
                    val = parseComment();
                else
                    val = parseValue();

                if (name.equals("W")) {
                    node.setAction(GameNode.Action.PUT_STONE);
                    node.setIntersection(Intersection.get(val, game));
                    node.setPlayer(game.getWhitePlayer());
                } else if (name.equals("B")) {
                    node.setAction(GameNode.Action.PUT_STONE);
                    node.setIntersection(Intersection.get(val, game));
                    node.setPlayer(game.getBlackPlayer());
                } else if (name.equals("AB")) {
                    node.addSetup(game.getBlackPlayer(), Intersection.get(val, game));

                } else if (name.equals("AW")) {
                    node.addSetup(game.getWhitePlayer(), Intersection.get(val, game));

                } else if (name.equals("AE")) {
                    node.addSetup(null, Intersection.get(val, game));

                } else if (name.equals("LB")) {
                    node.addLabel(val);

                } else if (name.equals("FF")) {
                    node.setProperty(name, val);
                    x = Integer.parseInt(val);
                    if (x < 1 || x > 4)
                        throw sgfError("Invalid SGF Version! (" + x + ")");
                } else if (name.equals("GM")) {
                    node.setProperty(name, val);
                    if (!is_root) throw sgfError("GM property in non-root node!");
                    if (Integer.parseInt(val) != GM_CODE && Integer.parseInt(val) != 1)
                        throw sgfError("Not a RageGO or Go game!");
                } else if (name.equals("SZ")) {
                    node.setProperty(name, val);
                    if (!is_root) throw sgfError("GM property in non-root node!");
                    Dimension dim = new Dimension();
                    String sp[] = val.split(":");
                    if (sp.length == 1) {
                        x = Integer.parseInt(sp[0]);
                        //noinspection SuspiciousNameCombination
                        dim.setSize(x, x);
                    } else if (sp.length == 2) {
                        x = Integer.parseInt(sp[0]);
                        y = Integer.parseInt(sp[1]);
                        dim.setSize(x, y);
                    } else {
                        throw sgfError("Malformed boardsize!");
                    }

                    // TODO : ??


                } else {
                    node.setProperty(name, val);

                }
            }
        }
    }

    private String parseValue() throws IOException {
        int ttype = tokenizer.nextToken();
        if (ttype != '[')
            throw sgfError("Property missing opening '['.");

        StringBuilder sb = new StringBuilder(256);
        boolean quoted = false;
        while (true) {
            int c = reader.read();
            if (c < 0)
                throw sgfError("Property runs to EOF.");

            if (!quoted) {
                if (c == ']') break;
                if (c == '\\')
                    quoted = true;
                else {
                    if (c != '\r' && c != '\n')
                        sb.append((char) c);
                }
            } else {
                quoted = false;
                sb.append(c);
            }
        }

        return sb.toString();
    }

    private String parseComment() throws IOException {
        int ttype = tokenizer.nextToken();
        if (ttype != '[')
            throw sgfError("Comment missing opening '['.");

        StringBuilder sb = new StringBuilder(4096);
        boolean quoted = false;
        while (true) {
            int c = reader.read();
            if (c < 0)
                throw sgfError("Comment runs to EOF.");

            if (!quoted) {
                if (c == ']') break;

                if (c == '\\')
                    quoted = true;
                else {
                    sb.append((char) c);
                }
            } else {
                quoted = false;
                sb.append(c);
            }
        }

        return sb.toString();
    }

    /**
     * Parse a branch in the game node.
     * A branch start with '(' and close with ')'. The first node in branch is a child of node given.
     *
     * @param node    The parent node for this branch
     * @param is_root True if it's the root branch
     * @return The first child of branch
     * @throws IOException
     */
    private GameNode parseBranch(GameNode node, boolean is_root) throws IOException {
        int ttype = tokenizer.nextToken();
        if (ttype != '(')
            throw sgfError("Missing '(' at head of game tree.");

        GameNode child = parseNode(node, is_root);

        ttype = tokenizer.nextToken();
        if (ttype != ')')
            throw sgfError("Game tree not closed!");

        return child;
    }
}
