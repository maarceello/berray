package com.berray.tests.level;

import com.berray.GameObject;
import com.berray.math.Vec2;

public class LevelGameObject extends GameObject {
  private final int tileWidth;
  private final int tileHeight;


  public LevelGameObject(int tileWidth, int tileHeight) {
    this.tileWidth = tileWidth;
    this.tileHeight = tileHeight;
  }

  public Vec2 tile2Pos(Vec2 pos) {
    return pos.scale(tileWidth, tileHeight);
  }

  public Vec2 tile2Pos(int x, int y) {
    return new Vec2(x * tileWidth, y * tileHeight);
  }
}
