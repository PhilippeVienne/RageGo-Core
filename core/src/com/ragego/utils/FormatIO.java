package com.ragego.utils;

import com.ragego.engine.GameNode;
import com.ragego.engine.GameBoard;

import java.io.File;
import java.io.IOException;

/**
 * Declare which actions could be performed by a IO Format.
 * A reader is a class which could read and write. Each format should be used for Go files.
 * @author Philippe Vienne
 * @since 1.0
 */
public interface FormatIO {

    /**
     * Execute {@link #write(java.io.File,com.ragego.engine.GameBoard)} with data stored in this class.
     * @return See {@link #write(java.io.File,com.ragego.engine.GameBoard)}
     */
    public boolean write() throws IOException;

    /**
     * Execute {@link #write(java.io.File,com.ragego.engine.GameBoard)} with file stored in this class and param.
     * @param game The game to save
     * @return See {@link #write(java.io.File,com.ragego.engine.GameBoard)}
     */
    public boolean write(GameBoard game) throws IOException;

    /**
     * Write game data to a file.
     *
     * @param file The file to write in (erase previous data)
     * @param game The game to save
     * @return true on success, false on failure
     */
    public boolean write(File file, GameBoard game) throws IOException;

    /**
     * Execute {@link #readRaw(java.io.File)} with data stored in this class.
     * @return See {@link #readRaw(java.io.File)}
     */
    public GameNode[] readRaw() throws IOException;

    /**
     * Read raw data from a file.
     * This functon is suitable to get a raw data from a file.
     * The board snaps extracted are just representative of board at some given times.
     *
     * @param file File to read
     * @return An array of sucessing position during the game.
     *         Index 0 is the first state and last index is the last state.
     */
    public GameNode[] readRaw(File file) throws IOException;

    /**
     * Execute {@link #read(java.io.File,com.ragego.engine.GameBoard)} with data stored in this class.
     * @return See {@link #read(java.io.File,com.ragego.engine.GameBoard)}
     */
    public boolean read() throws IOException;

    /**
     * Execute {@link #read(java.io.File,com.ragego.engine.GameBoard)} with file stored in this class and param.
     * @param game The game to save
     * @return See {@link #read(java.io.File,com.ragego.engine.GameBoard)}
     */
    public boolean read(GameBoard game) throws IOException;

    /**
     * Load a file in a Game.
     * Load a game in an empty {@link com.ragego.engine.GameBoard}. This load data about players, positions, stones. If
     * the format could not store this data, it should create default data.
     *
     * @param file The file to load
     * @param game An empty GameBoard (previous data could be erased)
     * @return true on success, false on all other state. In case of {@link java.io.IOException}, throw the error.
     */
    public boolean read(File file, GameBoard game) throws IOException;

}
