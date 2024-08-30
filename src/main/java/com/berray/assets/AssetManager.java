package com.berray.assets;

/** Asset manager interface. */
public interface AssetManager {

  Asset getAsset(String name);

  Asset getAsset(String name, Object type);

}
