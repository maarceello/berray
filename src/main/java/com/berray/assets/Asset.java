package com.berray.assets;

public class Asset {
  private final String name;
  private final AssetType type;
  private final Object asset;

  public Asset(String name, AssetType type, Object asset) {
    this.name = name;
    this.type = type;
    this.asset = asset;
  }

  public AssetType getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  public <E> E getAsset() {
    return (E) asset;
  }
}
