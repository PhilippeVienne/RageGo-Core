package com.ragego.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

/**
 * Create an asset manager for RageGo Game assets.
 */
public class RageGoAssetManager extends AssetManager {

    protected static final FileHandleResolver fileHandleResolver = new FileHandleResolver() {
        @Override
        public FileHandle resolve(String fileName) {
            return Gdx.files.classpath(fileName);
        }
    };

    public RageGoAssetManager() {
        super(RageGoAssetManager.fileHandleResolver);
        setLoader(TiledMap.class, "tmx", new TmxMapLoader(fileHandleResolver));
    }
}
