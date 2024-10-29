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

  public Rect(Rect other) {
    this.x = other.x;
    this.y = other.y;
    this.width = other.width;
    this.height = other.height;
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

  public Vec2 getPos() {
    return new Vec2(x, y);
  }

  public Vec2 getSize() {
    return new Vec2(width, height);
  }

  public List<Vec2> getPoints() {
    List<Vec2> points = new ArrayList<>();
    points.add(new Vec2(x, y));
    points.add(new Vec2(x, y + height));
    points.add(new Vec2(x + width, y));
    points.add(new Vec2(x + width, y + height));
    return points;
  }

  public Rect moveBy(Vec2 delta) {
    return new Rect(x + delta.getX(), y + delta.getY(), width, height);
  }

  public Raylib.Rectangle toRectangle() {
    return new Jaylib.Rectangle(x, y, width, height);
  }

  @Override
  public String toString() {
    return String.format("(%.3f, %.3f - %.3f, %.3f)", x, y, width, height);
  }

  public boolean contains(Vec2 point) {
    return contains(point.getX(), point.getY());
  }

  public boolean contains(float x, float y) {
    return x >= this.x && x <= this.x + this.width &&
        y >= this.y && y <= this.y + this.height;
  }
}
