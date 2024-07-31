package com.berray.math;

import com.raylib.Jaylib;
import com.raylib.Raylib;

import java.util.Objects;

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

  public static Vec2 down() {
    return new Vec2(0.0f, 1.0f);
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
    return len == 0 ? origin() : new Vec2(this.x / len, this.y / len);
  }

  public float length() {
    return (float) Math.sqrt(this.x * this.x + this.y * this.y);
  }

  public float lengthSquared() {
    return this.x * this.x + this.y * this.y;
  }

  public Vec2 sub(Vec2 other) {
    return new Vec2(this.x - other.getX(), this.y - other.getY());
  }

  public Vec2 move(Vec2 speed) {
    return add(speed);
  }

  public Vec2 add(Vec2 other) {
    return new Vec2(this.x + other.getX(), this.y + other.getY());
  }

  public float angle(Vec2 other) {
    return (float) Math.toDegrees(Math.atan2(this.y - other.y, this.x - other.x));
  }



  public Vec2 negate() {
    return new Vec2(-x, -y);
  }

  public float dot(Vec2 other) {
    return this.x * other.x + this.y * other.y;
  }

  public float cross(Vec2 other) {
    return this.x * other.y - this.y * other.x;
  }

  /**
   * Get the projection of a vector onto another vector.
   */
  public Vec2 project(Vec2 on) {
    return on.scale(on.dot(this) / on.length());
  }

  /**
   * Get the rejection of a vector onto another vector.
   */
  public Vec2 reject(Vec2 on) {
    return this.sub(this.project(on));
  }

  public Vec2 normal() {
    return new Vec2(this.y, -this.x);
  }

  public Raylib.Vector2 toVector2() {
    return new Jaylib.Vector2(x, y);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Vec2 vec2 = (Vec2) o;
    return Float.compare(x, vec2.x) == 0 && Float.compare(y, vec2.y) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y);
  }

  @Override
  public String toString() {
    return String.format("(%.3f, %.3f)", x,y);
  }
}
