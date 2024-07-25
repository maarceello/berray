package com.berray.components.core;

import com.berray.math.Vec2;

public enum AnchorType {
  TOP_LEFT(-1, -1),
  TOP(0, -1),
  TOP_RIGHT(1, -1),
  LEFT(-1, 0),
  CENTER(0, 0),
  RIGHT(1, 0),
  BOTTOM_LEFT(-1, 1),
  BOTTOM(0, 1),
  BOTTOM_RIGHT(1, 1);

  private int x;
  private int y;

  AnchorType(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public Vec2 getAnchorPoint(Vec2 size) {
    float w2 = size.getX() / 2.0f;
    float h2 = size.getY() / 2.0f;

    float anchorX = w2 + getX() * w2;
    float anchorY = h2 + getY() * h2;
    return new Vec2(-anchorX, -anchorY);
  }
}
