package com.berray.assets;

import com.raylib.Raylib;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.raylib.Raylib.Texture;
import static com.raylib.Raylib.Music;
import static com.raylib.Jaylib.LoadTexture;
import static com.raylib.Jaylib.LoadMusicStream;

public class DefaultAssetManager implements AssetManager {
  /** already read assets. */
  private final Map<String, Asset> assets = new HashMap<>();
  /** asset bundle which can read additional assets. */
  private final AssetBundle assetBundle;

  public DefaultAssetManager(AssetBundle assetBundle) {
    this.assetBundle = assetBundle;
  }

  @Override
  public void addAsset(Asset asset) {
    assets.put(asset.getName(), asset);
  }

  public void loadSprite(String name, BufferedImage image) {
    try {
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      ImageIO.write(image, "png", output);
      byte[] pngBytes = output.toByteArray();
      Raylib.Image raylibImage = Raylib.LoadImageFromMemory(".png", pngBytes, pngBytes.length);
      Raylib.Texture texture = Raylib.LoadTextureFromImage(raylibImage);
      addAsset(new Asset(name, AssetType.SPRITE, texture));
    } catch (IOException e) {
      throw new IllegalStateException("cannot add asset "+name, e);
    }
  }

  @Override
  public void loadSprite(String name, String path) {
    Texture sprite = LoadTexture(path);
    assets.put(name, new Asset(name, AssetType.SPRITE, sprite));
  }

  @Override
  public void loadSpriteSheet(String name, String path, SpriteSheet spriteSheet) {
    loadSprite(name+"_texture", path);
    spriteSheet.textureAsset(name+"_texture");
    spriteSheet.slice(this);
    assets.put(name, new Asset(name, AssetType.SPRITE_SHEET, spriteSheet));
  }

  public void loadSpriteSheet(String name, SpriteSheet spriteSheet) {
    spriteSheet.slice(this);
    assets.put(name, new Asset(name, AssetType.SPRITE_SHEET, spriteSheet));
  }


  @Override
  public void loadMusic(String name, String path) {
    Music music = LoadMusicStream(path);
    assets.put(name, new Asset(name, AssetType.MUSIC, music));
  }

  @Override
  public void loadSpriteAtlas(String name, String path, SpriteAtlas atlas) {
    loadSprite(name+"_texture", path);
    atlas.textureAsset(name+"_texture");
    atlas.slice(this);
    addSpriteAtlas(name, atlas);
  }

  public void addSpriteAtlas(String name, SpriteAtlas atlas) {
    if (atlas == null) {
      throw  new IllegalArgumentException("atlas must not be null");
    }
    // add atlas to asset manager
    assets.put(name, new Asset(name, AssetType.SPRITE_ATLAS, atlas));
    // also add all individual sprite sheets
    atlas.getSheets().forEach((spriteSheetName, spriteSheet) -> {
      assets.put(spriteSheetName, new Asset(spriteSheetName, AssetType.SPRITE_SHEET, spriteSheet));
    });
  }


  @Override
  public Asset getAsset(String name) {
    return assets.get(name);
  }

  @Override
  public Texture getSprite(String name) {
    Asset asset = assets.get(name);
    if (asset != null && asset.getAsset() instanceof Texture) {
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


  @Override
  public Music getMusic(String name) {
    Asset asset = assets.get(name);
    if (asset != null && asset.getAsset() instanceof Music) {
      return (Music) asset.getAsset();
    }
    return null;
  }
}