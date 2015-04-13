package com.ragego.gui;

import com.badlogic.gdx.Game;
import com.ragego.gui.screens.GoGameScreen;

public class RageGoGame extends Game {

	@Override
	public void create() {
		setScreen(new GoGameScreen());
	}

	@Override
	public void render () {
		super.render();
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void resume() {
		super.resume();
	}

	@Override
	public void pause() { super.pause(); }

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}
}
