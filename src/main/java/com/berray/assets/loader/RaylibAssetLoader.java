package com.berray.assets.loader;

import com.berray.assets.Asset;
import com.berray.assets.AssetType;
import com.berray.assets.AssetUtil;
import com.raylib.Raylib;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static com.raylib.Raylib.LoadTexture;

public class RaylibAssetLoader implements AssetLoader {
  private List<String> supportedExtensions = Arrays.asList("png");

  @Override
  public Asset loadAsset(String name, String params, ImageInputStream stream) {
    Path path = FileSystems.getDefault().getPath(name);
    // is the file a real file on the file system?
    if (Files.isRegularFile(path)) {
      // yes, load the file directly
      Raylib.Texture sprite = LoadTexture(name);
      return new Asset(name, AssetType.SPRITE, sprite);
    }
    try {
      // no, try to load the file from memory
      byte[] bytes = AssetUtil.toByteArray(stream);
      Raylib.Image raylibImage = Raylib.LoadImageFromMemory(".png", bytes, bytes.length);
      Raylib.Texture texture = Raylib.LoadTextureFromImage(raylibImage);
      return new Asset(name, AssetType.SPRITE, texture);
    } catch (IOException e) {
      throw new IllegalStateException("cannot load asset " + name, e);
    }
  }

  @Override
  public boolean canLoad(String name, ImageInputStream stream) {
    for (String extension : supportedExtensions) {
      if (name.endsWith(extension)) {
        return true;
      }
    }
    return false;
  }
}
