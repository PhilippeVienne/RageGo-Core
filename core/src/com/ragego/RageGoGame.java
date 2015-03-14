package com.ragego;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.ragego.screens.MenuScreen;

public class RageGoGame extends Game {
	SpriteBatch batch;
	Texture img;
	
	@Override
	public void create () {
        setScreen(new MenuScreen());
        Gdx.input.setCursorPosition(50,50);
	}

	@Override
	public void render () {
        super.render();
        System.out.println("Cursor is on "+Gdx.input.getX()+","+Gdx.input.getY());
        Gdx.graphics.setContinuousRendering(false);
	}
}
