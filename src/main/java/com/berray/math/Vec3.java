package com.berray.math;

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
