package com.berray.assets;


import com.berray.assets.loader.AssetLoader;
import com.berray.assets.loader.AssetLoaders;
import com.raylib.Raylib;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * {@link AssetManager} which reads assets from the local file system.
 */
public class DefaultAssetManager extends BaseAssetManager {
  private Map<String, Asset> assets = new HashMap<>();

  private AssetLoaders loaders;

  /**
   * Root folder of the assets.
   */
  private final Path rootFolder;

  public DefaultAssetManager(AssetLoaders assetLoaders, Path rootFolder) {
    this.loaders = assetLoaders;
    this.rootFolder = rootFolder;
  }

  @Override
  public Asset getAsset(String name) {
    // do we have parameters in the asset name?
    int parameterIndex = name.indexOf("#");
    if (parameterIndex > 0) {
      // yes. separate the file name from the parameters
      String localAssetName = name.substring(0, parameterIndex);
      String remaining = name.substring(parameterIndex + 1, name.length());
      // get the asset. if it does not exist, return null
      Asset asset = assets.get(localAssetName);
      if (asset == null) {
        return null;
      }

      // if the asset is indeed an asset manager, get the asset from the nested asset manager
      Object assetValue = asset.getAsset();
      if (assetValue instanceof AssetManager) {
        AssetManager nestedAssetManager = (AssetManager) assetValue;
        return nestedAssetManager.getAsset(remaining);
      }
      // asset is not an asset manager...fail with exception
      throw new IllegalStateException("asset " + name + " requested a nested asset manager, but the asset type is " + assetValue.getClass().getSimpleName());
    }

    // no parameters. get the asset directly
    return assets.get(name);
  }

  public Asset loadAsset(String name, String path) {
    Scanner scanner = new Scanner(path).useDelimiter("[/#]+");
    ResourceFile file = findFile(rootFolder, path, scanner);
    String filePath = file.file.toAbsolutePath().toString();
    String paramString = path.substring(file.parameterIndex);

    try (ImageInputStream stream = new MemoryCacheImageInputStream(Files.newInputStream(file.file))) {
      for (AssetLoader loader : loaders) {
        if (loader.canLoad(filePath, stream)) {
          Asset asset = loader.loadAsset(filePath, paramString, stream);
          if (asset != null) {
            assets.put(name, asset);
          }
          return asset;
        }
      }
    } catch (IOException e) {
      throw new IllegalStateException("cannot load asset " + filePath, e);
    }
    throw new IllegalStateException("cannot load asset " + filePath + ": no valid loader found");
  }

  private ResourceFile findFile(Path currentFolder, String pathString, Scanner scanner) {
    if (!scanner.hasNext()) {
      return new ResourceFile(currentFolder, pathString.length());
    }
    String part = scanner.next();
    if (part.equals("..") || part.equals(".")) {
      throw new IllegalStateException("back traversal in resources folder (..) is not allowed");
    }
    // resolve path
    Path path = currentFolder.resolve(part);
    // does this path exists?
    if (!Files.exists(path)) {
      throw new IllegalStateException("path part " + part + " in path " + pathString + " not found");
    }

    // if it is a regular file, return it.
    if (Files.isRegularFile(path)) {
      return new ResourceFile(path, scanner.match().end());
    }

    return findFile(path, pathString, scanner);
  }

  private static class ResourceFile {
    private Path file;
    /**
     * Index of the path part where the filename is done and the parameter string starts.
     */
    private int parameterIndex;

    public ResourceFile(Path file, int parameterIndex) {
      this.file = file;
      this.parameterIndex = parameterIndex;
    }
  }

  public void loadSprite(String name, String path) {
    // preload sprite
    Asset asset = loadAsset(name, path);
    if (asset.getType() != AssetType.SPRITE) {
      throw new IllegalStateException("asset " + name + " from path " + path + " is not a sprite, but a " + asset.getType());
    }
    assets.put(name, asset);
  }

  public void loadSprite(String name, RenderedImage bufferedImage) {
    try {
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      ImageIO.write(bufferedImage, "png", output);
      byte[] pngData = output.toByteArray();
      Raylib.Image image = Raylib.LoadImageFromMemory(".png", pngData, pngData.length);
      Raylib.Texture texture = Raylib.LoadTextureFromImage(image);
      assets.put(name, new Asset(name, AssetType.SPRITE, texture));
    } catch (IOException e) {
      throw  new IllegalStateException("cannot load sprite "+name+" from BufferedImage");
    }

  }


  public void loadSpriteSheet(String name, String path, SpriteSheet spriteSheet) {
    loadSprite(name + "_texture", path);
    spriteSheet.textureAsset(name + "_texture");
    spriteSheet.slice(this);
    assets.put(name, new Asset(name, AssetType.SPRITE_SHEET, spriteSheet));
  }

  public void loadSpriteSheet(String name, SpriteSheet spriteSheet) {
    spriteSheet.slice(this);
    assets.put(name, new Asset(name, AssetType.SPRITE_SHEET, spriteSheet));
  }


  public void loadMusic(String name, String path) {
    throw new UnsupportedOperationException("not yet implemented");
  }

  public void loadSpriteAtlas(String name, String path, SpriteAtlas atlas) {
    loadSprite(name + "_texture", path);
    atlas.textureAsset(name + "_texture");
    atlas.slice(this);
    // add atlas to asset manager
    assets.put(name, new Asset(name, AssetType.SPRITE_ATLAS, atlas));
    // also add all individual sprite sheets
    atlas.getSheets().forEach((spriteSheetName, spriteSheet) -> assets.put(spriteSheetName, new Asset(spriteSheetName, AssetType.SPRITE_SHEET, spriteSheet)));
  }
}
