package com.berray.assets;

import com.berray.math.Rect;
import com.raylib.Raylib;

import java.util.HashMap;
import java.util.Map;

public class SpriteSheet {
  private int x = 0;
  private int y = 0;
  private int width = -1;
  private int height = -1;
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

  public SpriteSheet x(int x) {
    this.x = x;
    return this;
  }

  public SpriteSheet y(int y) {
    this.y = y;
    return this;
  }

  public SpriteSheet width(int width) {
    this.width = width;
    return this;
  }

  public SpriteSheet height(int height) {
    this.height = height;
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

  public boolean hasAnimations() {
    return !animations.isEmpty();
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
    int textureWidth = width > 0 ? width : texture.width();
    int textureHeight = height > 0 ? height : texture.height();

    this.spriteWidth = textureWidth / sliceX;
    this.spriteHeight = textureHeight / sliceY;

    for (Animation animation : animations.values()) {
      int start = animation.getFrom();
      int end = animation.getTo();
      int direction = Integer.signum(end - start);
      int numFrames = animation.getNumFrames();
      for (int frame = start, i = 0; i < numFrames; frame += direction, i++) {
        int x = (frame) % sliceX;
        int y = (frame) / sliceX;
        Rect rect = new Rect(this.x + x * spriteWidth, this.y + y * spriteHeight, spriteWidth, spriteHeight);
        animation.addFrame(rect);
      }
    }

    this.texture = texture;
  }

  public static SpriteSheet spriteSheet() {
    return new SpriteSheet();
  }

  public Rect getFrame(int frameNo) {
    int x = (frameNo) % sliceX;
    int y = (frameNo) / sliceY;
    return new Rect(this.x + x * spriteWidth, this.y + y * spriteHeight, spriteWidth, spriteHeight);
  }
}
