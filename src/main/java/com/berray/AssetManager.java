package com.berray;

import java.util.HashMap;
import java.util.Map;

import static com.raylib.Raylib.Texture;
import static com.raylib.Jaylib.LoadTexture;

class Asset {
  private final String name;
  private final int type;
  private final Object asset;

  public Asset(String name, int type, Object asset) {
    this.name = name;
    this.type = type;
    this.asset = asset;
  }

}

// TODO: Should be generic th all files not Sprites later

public class AssetManager {
  private final static Map<String, Asset> assets = new HashMap<>();

  public static void loadSprite(String name, String path) {
    Texture sprite = LoadTexture(path);
    assets.put(name, new Asset(name, 0, sprite));
  }

}