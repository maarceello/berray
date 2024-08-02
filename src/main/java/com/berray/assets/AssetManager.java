package com.berray.assets;

import java.util.HashMap;
import java.util.Map;

import static com.raylib.Jaylib.Texture;
import static com.raylib.Jaylib.Music;
import static com.raylib.Jaylib.LoadTexture;
import static com.raylib.Jaylib.LoadMusicStream;

public class AssetManager {
  private final static Map<String, Asset> assets = new HashMap<>();

  public static void loadSprite(String name, String path) {
    Texture sprite = LoadTexture(path);
    assets.put(name, new Asset(name, AssetType.SPRITE, sprite));
  }

  public static void loadSprite(String name, String path, SpriteSheet spriteSheet) {
    Texture sprite = LoadTexture(path);
    spriteSheet.slice(sprite);
    assets.put(name, new Asset(name, AssetType.SPRITE_SHEET, spriteSheet));
  }

  public static void loadMusic(String name, String path) {
    Music music = LoadMusicStream(path);
    assets.put(name, new Asset(name, AssetType.MUSIC, music));
  }

  public static void loadSpriteAtlas(String name, String path, SpriteAtlas atlas) {
    Texture sprite = LoadTexture(path);
    atlas.slice(sprite);
    // add atlas to asset manager
    assets.put(name, new Asset(name, AssetType.SPRITE_ATLAS, atlas));
    // also add all individual sprite sheets
    atlas.getSheets().forEach((spriteSheetName, spriteSheet) -> {
      assets.put(spriteSheetName, new Asset(spriteSheetName, AssetType.SPRITE_SHEET, spriteSheet));
    });
  }

  public static Asset getAsset(String name) {
    Asset asset = assets.get(name);
    return asset;
  }

  public static Texture getSprite(String name) {
    Asset asset1 = assets.get(name);
    Asset asset = asset1;
    if (asset != null && asset.getAsset() instanceof Texture) {
      return (Texture) asset.getAsset();
    }
    return null;
  }

  public static Music getMusic(String name) {
    Asset asset = assets.get(name);
    if (asset != null && asset.getAsset() instanceof Music) {
      return (Music) asset.getAsset();
    }
    return null;
  }
}