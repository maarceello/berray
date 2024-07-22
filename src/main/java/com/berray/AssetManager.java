package com.berray;

import java.util.HashMap;
import java.util.Map;

import static com.raylib.Jaylib.Texture;
import static com.raylib.Jaylib.Music;
import static com.raylib.Jaylib.LoadTexture;
import static com.raylib.Jaylib.LoadMusicStream;

class Asset {
  private final String name;
  private final int type;
  private final Object asset;

  public Asset(String name, int type, Object asset) {
    this.name = name;
    this.type = type;
    this.asset = asset;
  }

  public Object getAsset() {
    return asset;
  }
}

public class AssetManager {
  private final static Map<String, Asset> assets = new HashMap<>();

  public static void loadSprite(String name, String path) {
    Texture sprite = LoadTexture(path);
    assets.put(name, new Asset(name, 0, sprite));
  }

  public static void loadMusic(String name, String path) {
    Music music = LoadMusicStream(path);
    assets.put(name, new Asset(name, 1, music));
  }

  public static Texture getSprite(String name) {
    Asset asset = assets.get(name);
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