package com.ragego.gui.screens;

import com.badlogic.gdx.audio.Music;

/**
 * Screen contains a music background to play.
 */
public interface MusicalScreen {

    Music getBackgroundMusic();
    void playMusic();
    void stopMusic();

}
