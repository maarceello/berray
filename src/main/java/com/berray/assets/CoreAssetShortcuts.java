package com.berray.assets;

public interface CoreAssetShortcuts {
  AssetManager getAssetManager();


  default void loadSprite(String name, String path) {
    getAssetManager().loadSprite(name, path);
  }

  default  void loadMusic(String name, String path) {
    getAssetManager().loadMusic(name, path);
  }

  default void loadSpriteAtlas(String name, String path, SpriteAtlas atlas) {
    getAssetManager().loadSpriteAtlas(name, path, atlas);
  }

  default void loadSpriteSheet(String name, String path, SpriteSheet spriteSheet) {
    getAssetManager().loadSpriteSheet(name, path, spriteSheet);
  }
}
