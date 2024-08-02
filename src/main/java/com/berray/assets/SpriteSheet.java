package com.berray.assets;

import com.berray.math.Rect;
import com.raylib.Raylib;

import java.util.HashMap;
import java.util.Map;

public class SpriteSheet {
  private int sliceX = 1;
  private int sliceY = 1;
  private Map<String, Animation> animations = new HashMap<>();
  private Raylib.Texture texture;
  private int spriteWidth;
  private int spriteHeight;

  public SpriteSheet anim(String animationName, Animation animation) {
    this.animations.put(animationName, animation);
    return this;
  }

  /**
   * Animation with only one frame.
   */
  public SpriteSheet anim(String animationName, int frame) {
    this.animations.put(animationName, Animation.anim()
        .from(frame).to(frame));
    return this;
  }

  public SpriteSheet sliceX(int sliceX) {
    this.sliceX = sliceX;
    return this;
  }

  public SpriteSheet sliceY(int sliceY) {
    this.sliceY = sliceY;
    return this;
  }

  public Animation getAnimation(String animationName) {
    return animations.get(animationName);
  }

  public Raylib.Texture getTexture() {
    return texture;
  }

  public int getSpriteWidth() {
    return spriteWidth;
  }

  public int getSpriteHeight() {
    return spriteHeight;
  }

  /**
   * Slices the texture according to the slice parameters.
   */
  public void slice(Raylib.Texture texture) {
    int textureWidth = texture.width();
    int textureHeight = texture.height();

    this.spriteWidth = textureWidth / sliceX;
    this.spriteHeight = textureHeight / sliceY;

    for (Animation animation : animations.values()) {
      for (int frame = 0; frame <= animation.getNumFrames(); frame++) {
        int x = (frame + animation.getFrom()) % sliceX;
        int y = (frame + animation.getFrom()) / sliceY;
        Rect rect = new Rect(x * spriteWidth, y * spriteHeight, spriteWidth, spriteHeight);
        animation.addFrame(rect);
      }
    }

    this.texture = texture;
  }

  public static SpriteSheet spriteSheet() {
    return new SpriteSheet();
  }

}
