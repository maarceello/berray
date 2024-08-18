package com.berray.assets;


import com.raylib.Raylib;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** {@link AssetBundle} which reads assets from the local file system.  */
public class FolderAssetBundle implements AssetBundle, AssetManager {

  private List<AssetLoader> loaders = Arrays.asList(
      new RaylibAssetLoader()
  );

  /** Root folder of the assets. */
  private final Path rootFolder;

  public FolderAssetBundle(Path rootFolder) {
    this.rootFolder = rootFolder;
  }

  @Override
  public Iterable<String> getAssetNames() {
    try {
      return Files.walk(rootFolder)
          // only return files, no folders
          .filter(Files::isRegularFile)
          // only return the file name
          .map(Path::toString)
          .collect(Collectors.toList());
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public InputStream getAssetData(String name) {
    Path assetPath = rootFolder.resolve(name).toAbsolutePath();
    if (!assetPath.startsWith(rootFolder)) {
      throw new IllegalArgumentException("moving outside of the rootfolder is not allowed: "+name);
    }
    try {
      return Files.newInputStream(assetPath);
    } catch (IOException e) {
      throw new IllegalStateException("cannot read file "+assetPath, e);
    }
  }

  private final Map<String, Asset> assets = new HashMap<>();

  private Asset loadAsset(String path) {
    for (AssetLoader loader : loaders) {
      if (loader.canLoad(path, this)) {
        return loader.loadAsset(path, this);
      }
    }
    throw new IllegalStateException("cannot load asset "+path);
  }

  @Override
  public void addAsset(Asset asset) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void loadSprite(String name, String path) {
    // preload sprite
    Asset asset = loadAsset(path);
    if (asset.getType() != AssetType.SPRITE) {
      throw new IllegalStateException("asset "+name+" from path "+path+" is not a sprite, but a "+asset.getType());
    }
    assets.put(name, asset);
  }

  @Override
  public void loadSpriteSheet(String name, String path, SpriteSheet spriteSheet) {
    loadSprite(name+"_texture", path);
    spriteSheet.textureAsset(name+"_texture");
    spriteSheet.slice(this);
    assets.put(name, new Asset(name, AssetType.SPRITE_SHEET, spriteSheet));

  }

  @Override
  public void loadMusic(String name, String path) {
    throw new UnsupportedOperationException("not yet implemented");
  }

  @Override
  public Raylib.Music getMusic(String name) {
    throw new UnsupportedOperationException("not yet implemented");
  }

  @Override
  public void loadSpriteAtlas(String name, String path, SpriteAtlas atlas) {
    loadSprite(name+"_texture", path);
    atlas.textureAsset(name+"_texture");
    atlas.slice(this);
    // add atlas to asset manager
    assets.put(name, new Asset(name, AssetType.SPRITE_ATLAS, atlas));
    // also add all individual sprite sheets
    atlas.getSheets().forEach((spriteSheetName, spriteSheet) -> assets.put(spriteSheetName, new Asset(spriteSheetName, AssetType.SPRITE_SHEET, spriteSheet)));
  }

  @Override
  public Asset getAsset(String name) {
    return assets.get(name);
  }

  @Override
  public Raylib.Texture getSprite(String name) {
    Asset asset = assets.get(name);
    if (asset != null && asset.getAsset() instanceof Raylib.Texture) {
      return asset.getAsset();
    }
    throw new IllegalStateException("texture asset "+name+" not found");
  }

  @Override
  public SpriteSheet getSpriteSheet(String name) {
    Asset asset = assets.get(name);
    if (asset != null && asset.getAsset() instanceof SpriteSheet) {
      return asset.getAsset();
    }
    throw new IllegalStateException("sprite sheet asset "+name+" not found");
  }
}
