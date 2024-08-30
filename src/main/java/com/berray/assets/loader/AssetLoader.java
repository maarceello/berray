package com.berray.assets.loader;

import com.berray.assets.Asset;

import javax.imageio.stream.ImageInputStream;

/** Interface to load an asset. */
public interface AssetLoader {
  /** Reads and returns the asset. */
  Asset loadAsset(String assetPath, String params, ImageInputStream stream);

  /** Returns true when the loader can load the asset from the bundle. */
  boolean canLoad(String assetPath, ImageInputStream stream);

}
