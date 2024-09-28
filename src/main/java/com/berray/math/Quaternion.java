package com.berray.math;

public class Quaternion {
  private final float x;
  private final float y;
  private final float z;
  private final float w;

  public Quaternion(float x, float y, float z, float w) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.w = w;
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

  public float getW() {
    return w;
  }

  public Quaternion mul(Quaternion other) {
    float x = this.x * other.w + this.y * other.z - this.z * other.y + this.w * other.x;
    float y = -this.x * other.z + this.y * other.w + this.z * other.x + this.w * other.y;
    float z = this.x * other.y - this.y * other.x + this.z * other.w + this.w * other.z;
    float w = -this.x * other.x - this.y * other.y - this.z * other.z + this.w * other.w;
    return new Quaternion(x, y, z, w);
  }

  public Vec3 mul(Vec3 other) {
    float w = this.w;
    float x = this.x;
    float y = this.y;
    float z = this.z;
    float Vx = other.x;
    float Vy = other.y;
    float Vz = other.z;
    float ww = w * w;
    float w2 = w * 2;
    float wx2 = w2 * x;
    float wy2 = w2 * y;
    float wz2 = w2 * z;
    float xx = x * x;
    float x2 = x * 2;
    float xy2 = x2 * y;
    float xz2 = x2 * z;
    float yy = y * y;
    float yz2 = 2 * y * z;
    float zz = z * z;
    return new Vec3(
        ww * Vx + wy2 * Vz - wz2 * Vy +
            xx * Vx + xy2 * Vy + xz2 * Vz -
            zz * Vx - yy * Vx,
        xy2 * Vx + yy * Vy + yz2 * Vz +
            wz2 * Vx - zz * Vy + ww * Vy -
            wx2 * Vz - xx * Vy,
        xz2 * Vx + yz2 * Vy +
            zz * Vz - wy2 * Vx - yy * Vz +
            wx2 * Vy - xx * Vz + ww * Vz);
  }

  public static Quaternion identity() {
    return new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
  }


  public Vec3 toEuler() {
    float t = this.x * this.y + this.z * this.w;
    if (t > 0.4999) {
      float heading = (float) (2 * Math.atan2(this.x, this.w));
      float attitude = (float) (Math.PI / 2);
      float bank = 0;
      return new Vec3(heading, attitude, bank);
    }

    if (t < -0.4999) {
      float heading = (float) (-2 * Math.atan2(this.x, this.w));
      float attitude = (float) (-Math.PI / 2);
      float bank = 0;
      return new Vec3(heading, attitude, bank);
    }

    float sqx = this.x * this.x;
    float sqy = this.y * this.y;
    float sqz = this.z * this.z;
    float heading = (float) Math.atan2(2 * this.y * this.w - 2 * this.x * this.z, 1 - 2 * sqy - 2 * sqz);
    float attitude = (float) Math.asin(2 * t);
    float bank = (float) Math.atan2(2 * this.x * this.w - 2 * this.y * this.z, 1 - 2 * sqx - 2 * sqz);
    return new Vec3(heading, attitude, bank);
  }

  public Matrix4 toMatrix() {
    float xx = this.x * this.x;
    float xy = this.x * this.y;
    float xz = this.x * this.z;
    float xw = this.x * this.w;
    float yy = this.y * this.y;
    float yz = this.y * this.z;
    float yw = this.y * this.w;
    float zz = this.z * this.z;
    float zw = this.z * this.w;
    float a = 1 - 2 * (yy + zz);
    float b = 2 * (xy - zw);
    float c = 2 * (xz + yw);
    float e = 2 * (xy + zw);
    float f = 1 - 2 * (xx + zz);
    float g = 2 * (yz - xw);
    float i = 2 * (xz - yw);
    float j = 2 * (yz + xw);
    float k = 1 - 2 * (xx + yy);
    return new Matrix4(a, b, c, 0,
        e, f, g, 0,
        i, j, k, 0,
        0, 0, 0, 1);
  }


  public static Quaternion fromEuler(float heading, float attitude, float bank) {
    float c1 = (float) Math.cos(heading / 2);
    float s1 = (float) Math.sin(heading / 2);
    float c2 = (float) Math.cos(attitude / 2);
    float s2 = (float) Math.sin(attitude / 2);
    float c3 = (float) Math.cos(bank / 2);
    float s3 = (float) Math.sin(bank / 2);

    float w = c1 * c2 * c3 - s1 * s2 * s3;
    float x = s1 * s2 * c3 + c1 * c2 * s3;
    float y = s1 * c2 * c3 + c1 * s2 * s3;
    float z = c1 * s2 * c3 - s1 * c2 * s3;
    return new Quaternion(x, y, z, w);
  }




}
