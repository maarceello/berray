package com.berray.components.core;

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
}
