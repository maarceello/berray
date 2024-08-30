package com.berray.assets;

import com.raylib.Raylib;

public abstract class BaseAssetManager implements AssetManager {

  public static final String PROPERTY_SEPARATOR = "\\.";

  public Raylib.Music getMusic(String name) {
    throw new UnsupportedOperationException("not yet implemented");
  }

  @Override
  public Asset getAsset(String name, Object type) {
    Asset asset = getAsset(name);
    if (asset == null) {
      throw  new IllegalStateException("asset "+name+" not found");
    }
    if (!type.equals(asset.getType())) {
      throw  new IllegalStateException("asset "+name+" should be of type "+type+", but is "+asset.getType());
    }
    return asset;
  }

  public Raylib.Texture getSprite(String name) {
    return getAsset(name, AssetType.SPRITE).getAsset();
  }

  public SpriteSheet getSpriteSheet(String name) {
    return getAsset(name, AssetType.SPRITE_SHEET).getAsset();
  }

}
