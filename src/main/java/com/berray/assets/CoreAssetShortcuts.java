package com.berray.assets;

import java.awt.image.RenderedImage;

public interface CoreAssetShortcuts {
  DefaultAssetManager getAssetManager();


  default void loadSprite(String name, String path) {
    getAssetManager().loadSprite(name, path);
  }

  default void loadSprite(String name, RenderedImage image) {
    getAssetManager().loadSprite(name, image);
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
