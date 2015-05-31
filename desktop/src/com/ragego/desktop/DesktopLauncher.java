package com.ragego.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.ragego.gui.RageGoGame;

public class DesktopLauncher {
	private static boolean IS_PRODUCTION = false;

	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		if(IS_PRODUCTION) {
			config.resizable = false;
			config.fullscreen = true;
			config.vSyncEnabled = true;
		} else {
			config.fullscreen = false;
			config.height = 600;
			config.width = 1000;
			config.resizable = true;
		}

		config.title = "RageGo";

		new LwjglApplication(RageGoGame.getInstance(), config);
		if(IS_PRODUCTION)
			Gdx.graphics.setDisplayMode(Gdx.graphics.getDesktopDisplayMode().width, Gdx.graphics.getDesktopDisplayMode().height, true);
	}
}
