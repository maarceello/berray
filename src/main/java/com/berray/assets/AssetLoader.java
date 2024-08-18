package com.berray.assets;

import java.util.ResourceBundle;

/** Interface to load an asset. The bytes will be supplied by the {@link AssetBundle}. */
public interface AssetLoader {
  /** Reads and returns the asset. */
  Asset loadAsset(String name, AssetBundle bundle);

  /** Returns true when the loader can load the asset from the bundle. */
  boolean canLoad(String name, AssetBundle bundle);

}
