package com.berray.assets;

import java.io.InputStream;

/** Bundle with some assets. */
public interface AssetBundle {

  /** Returns all asset names this bundle manages. */
  Iterable<String> getAssetNames();

  /** Returns the bytes of the asset. */
  InputStream getAssetData(String name);
}
