package com.ragego.gui.screens;

import com.badlogic.gdx.audio.Music;

/**
 * Screen contains a music background to play.
 */
public interface MusicalScreen {

    /**
     * Getter for the music
     * @return The {@link Music} which should be played for this screen.
     */
    Music getBackgroundMusic();

    /**
     * Start playing music. The screen should manage all start playing.
     */
    void playMusic();

    /**
     * Stop the music.
     */
    void stopMusic();

}
