package com.berray.math;

public class Vec3 {
  float x;
  float y;
  float z;

  public Vec3() {
  }

  public Vec3(float x, float y, float z) {
    this.x = x;
    this.y = y;
    this.z = z;
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

  public float getZ() {
    return z;
  }

  public void setZ(float z) {
    this.z = z;
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


  public static Vec3 center() {
    return new Vec3(0,0,0);
  }
}
