package com.berray.assets;

import com.raylib.Raylib;

public interface AssetManager {
  void addAsset(Asset asset);

  void loadSprite(String name, String path);

  void loadSpriteSheet(String name, String path, SpriteSheet spriteSheet);

  void loadMusic(String name, String path);

  void loadSpriteAtlas(String name, String path, SpriteAtlas atlas);

  Asset getAsset(String name);

  Raylib.Texture getSprite(String name);

  SpriteSheet getSpriteSheet(String name);

  Raylib.Music getMusic(String name);
}
