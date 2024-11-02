package com.berray.math;

import com.raylib.Jaylib;
import com.raylib.Raylib;

public class Vec3 {
  public static final Vec3 ORIGIN = new Vec3(0, 0, 0);
  final float x;
  final float y;
  final float z;

  public Vec3() {
    this(0, 0, 0);
  }

  public Vec3(float x, float y, float z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public Vec3(Raylib.Vector3 other) {
    this.x = other.x();
    this.y = other.y();
    this.z = other.z();
  }

  public float getX() {
    return x;
  }


  public float getY() {
    return y;
  }


  public float getZ() {
    return z;
  }


  public float length() {
    return (float) Math.sqrt(x * x + y * y + z * z);
  }

  public Vec3 normalize() {
    float length = length();
    return new Vec3(x / length, y / length, z / length);
  }

  public Vec3 scale(float value) {
    return new Vec3(this.x * value, this.y * value, this.z * value);
  }

  public Vec3 scale(float scaleX, float scaleY, float scaleZ) {
    return new Vec3(this.x * scaleX, this.y * scaleY, this.z * scaleZ);
  }


  public Vec3 dot(Vec3 other) {
    return new Vec3(x * other.x, y * other.y, z * other.z);
  }

  public Vec3 cross(Vec3 other) {
    return new Vec3(y * other.z - z * other.y,
        -x * other.z + z * other.x,
        x * other.y - y * other.x);
  }

  public Vec3 add(Vec3 other) {
    return new Vec3(x + other.x, y + other.y, z + other.z);
  }

  public Vec3 sub(Vec3 other) {
    return new Vec3(x - other.x, y - other.y, z - other.z);
  }

  public Vec3 move(Vec3 speed) {
    return add(speed);
  }

  /** Transforms this {@link Vec3} to a {@link Vec2} by dropping the z component. */
  public Vec2 toVec2() {
    return new Vec2(x, y);
  }

  public Raylib.Vector3 toVector3() {
    return new Jaylib.Vector3(x, y, z);
  }

  public static Vec3 origin() {
    return ORIGIN;
  }

  @Override
  public String toString() {
    return "{" +
        "x=" + x +
        ", y=" + y +
        ", z=" + z +
        '}';
  }
}
