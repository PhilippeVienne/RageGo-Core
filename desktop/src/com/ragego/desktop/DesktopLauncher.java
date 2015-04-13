package com.ragego.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.ragego.gui.RageGoGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.height = 600;
		config.width = 1000;
		config.resizable = false; //Temporarily blocked for first tests

		config.title = "RageGo";

		new LwjglApplication(new RageGoGame(), config);
	}
}
