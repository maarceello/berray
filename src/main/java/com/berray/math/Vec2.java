package com.berray.math;

import com.raylib.Jaylib;
import com.raylib.Raylib;

public class Vec2 {
  private float x;
  private float y;

  public Vec2() {
  }

  public Vec2(float x, float y) {
    this.x = x;
    this.y = y;
  }

  public static Vec2 origin() {
    return new Vec2(0.0f, 0.0f);
  }

  public float getX() {
    return x;
  }

  public void setX(float x) {
    this.x = x;
  }

  public float getY() {
    return y;
  }

  public void setY(float y) {
    this.y = y;
  }

  public Raylib.Vector2 toVecor2() {
    return new Jaylib.Vector2(x, y);
  }
}
