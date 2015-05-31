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

    /**
     * File resolver that should be used for all assets.
     */
    protected static final FileHandleResolver fileHandleResolver = new FileHandleResolver() {
        @Override
        public FileHandle resolve(String fileName) {
            return Gdx.files.internal(fileName);
        }
    };

    /**
     * Create an asset manager with the file handle resolver from RageGo.
     */
    public RageGoAssetManager() {
        super(RageGoAssetManager.fileHandleResolver);
        setLoader(TiledMap.class, "tmx", new TmxMapLoader(fileHandleResolver));
    }
}
