package com.berray.assets;

import com.raylib.Raylib;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static com.raylib.Raylib.LoadTexture;

public class RaylibAssetLoader implements AssetLoader {
  private List<String> supportedExtensions = Arrays.asList("png");

  @Override
  public Asset loadAsset(String name, AssetBundle bundle) {
    Path path = FileSystems.getDefault().getPath(name);
    // is the file a real file on the file system?
    if (Files.isRegularFile(path)) {
      // yes, load the file directly
      Raylib.Texture sprite = LoadTexture(name);
      return new Asset(name, AssetType.SPRITE, sprite);
    }
    try {
      // no, try to load the file from memory
      InputStream stream = bundle.getAssetData(name);
      byte[] bytes = AssetUtil.toByteArray(stream);
      Raylib.Image raylibImage = Raylib.LoadImageFromMemory(".png", bytes, bytes.length);
      Raylib.Texture texture = Raylib.LoadTextureFromImage(raylibImage);
      return new Asset(name, AssetType.SPRITE, texture);
    } catch (IOException e) {
      throw new IllegalStateException("cannot load asset " + name, e);
    }
  }

  @Override
  public boolean canLoad(String name, AssetBundle bundle) {
    for (String extension : supportedExtensions) {
      if (name.endsWith(extension)) {
        return true;
      }
    }
    return false;
  }
}
