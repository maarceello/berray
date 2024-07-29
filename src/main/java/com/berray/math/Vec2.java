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

  public static Vec2 fromAngle(float angleDegree) {
    double rad = Math.toRadians(angleDegree);
    return new Vec2((float) Math.cos(rad), (float) Math.sin(rad));
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

  public Vec2 scale(float value) {
    return new Vec2(this.x * value, this.y * value);
  }

  public Vec2 scale(float scaleX, float scaleY) {
    return new Vec2(this.x * scaleX, this.y * scaleY);
  }

  public Vec2 normalize() {
    float len = length();
    return new Vec2(this.x /len, this.y / len);
  }

  public float length() {
    return (float) Math.sqrt(this.x * this.x + this.y * this.y);
  }
  public float lengthSquared() {
    return this.x * this.x + this.y * this.y;
  }

  public Vec2 sub(Vec2 other) {
    return new Vec2(this.x -other.getX(), this.y - other.getY());
  }

  public Vec2 move(Vec2 speed) {
    return add(speed);
  }
  public Vec2 add(Vec2 other) {
    return new Vec2(this.x +other.getX(), this.y +other.getY());
  }

  public float angle(Vec2 other) {
    return (float) Math.toDegrees(Math.atan2(this.y - other.y, this.x - other.x));
  }


  public Raylib.Vector2 toVector2() {
    return new Jaylib.Vector2(x, y);
  }

  public Vec2 negate() {
    return new Vec2(-x, -y);
  }

  public float dot(Vec2 other) {
    return this.x * other.x + this.y * other.y;
  }
}
