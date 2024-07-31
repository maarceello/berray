package com.berray.math;

import com.raylib.Jaylib;
import com.raylib.Raylib;

import java.util.ArrayList;
import java.util.List;

public class Rect {
  private float x;
  private float y;
  private float width;
  private float height;

  public Rect() {
  }

  public Rect(float x, float y, float width, float height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
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

  public float getWidth() {
    return width;
  }

  public void setWidth(float width) {
    this.width = width;
  }

  public float getHeight() {
    return height;
  }

  public void setHeight(float height) {
    this.height = height;
  }

  public List<Vec2> getPoints() {
    List<Vec2> points = new ArrayList<>();
    points.add(new Vec2(x, y));
    points.add(new Vec2(x, y + height));
    points.add(new Vec2(x + width, y));
    points.add(new Vec2(x + width, y + height));
    return points;
  }

  public Raylib.Rectangle toRectangle() {
    return new Jaylib.Rectangle(x, y, width, height);
  }
}
