package com.berray.math;

import com.raylib.Jaylib;
import com.raylib.Raylib;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

  public Rect(Vec2 pos, Vec2 size) {
    this.x = pos.getX();
    this.y = pos.getY();
    this.width = size.getX();
    this.height = size.getY();
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

  /** Returns this rectangle so that the width and height are positiv. */
  public Rect normalize() {
    return new Rect(
        Math.min(x, x+width),
        Math.min(y, y+height),
        Math.abs(width),
        Math.abs(height)
    );
  }


  /** Returns the center of the rectangle. */
  public Vec2 getCenter() {
    return new Vec2(x + width / 2, y + height / 2);
  }


  public boolean contains(Vec2 point) {
    return contains(point.getX(), point.getY());
  }

  public boolean contains(float x, float y) {
    return x >= this.x && x <= this.x + this.width &&
        y >= this.y && y <= this.y + this.height;
  }



  /**
   * Scales the rectangle 'other' so that is fits exactly in this rectangle. The destination rectangle is centered in
   * this rectangle.
   */
  public Rect getFitRectangle(Rect other) {
    float aspectRatio = other.width / other.height;
    float otherAspectRatio = width / height;

    float resizeFactor = (aspectRatio >= otherAspectRatio) ? (width / other.width) : (height / other.height);

    float newWidth = other.width * resizeFactor;
    float newHeight = other.height * resizeFactor;
    float newX = x + (width - newWidth) / 2.0f;
    float newY = y + (height - newHeight) / 2.0f;

    return new Rect(newX, newY, newWidth, newHeight);
  }


  public Raylib.Rectangle toRectangle() {
    return new Jaylib.Rectangle(x, y, width, height);
  }


  @Override
  public String toString() {
    return String.format("(%.3f, %.3f - %.3f, %.3f)", x, y, width, height);
  }
}
