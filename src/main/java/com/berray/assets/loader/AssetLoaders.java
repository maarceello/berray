package com.berray.assets.loader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * List of asset loaders.
 */
public class AssetLoaders implements Iterable<AssetLoader> {

  private List<AssetLoader> loaders = new ArrayList<>();


  public void addAssetLoader(AssetLoader loader) {
    loaders.add(loader);
  }

  @Override
  public Iterator<AssetLoader> iterator() {
    return loaders.iterator();
  }
}
