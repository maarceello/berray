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
    float resultx = this.x * other.w + this.y * other.z - this.z * other.y + this.w * other.x;
    float resulty = -this.x * other.z + this.y * other.w + this.z * other.x + this.w * other.y;
    float resultz = this.x * other.y - this.y * other.x + this.z * other.w + this.w * other.z;
    float resultw = -this.x * other.x - this.y * other.y - this.z * other.z + this.w * other.w;
    return new Quaternion(resultx, resulty, resultz, resultw);
  }

  public Vec3 mul(Vec3 other) {
    float thisw = this.w;
    float thisx = this.x;
    float thisy = this.y;
    float thisz = this.z;
    float otherx = other.x;
    float othery = other.y;
    float otherz = other.z;
    float ww = thisw * thisw;
    float w2 = thisw * 2;
    float wx2 = w2 * thisx;
    float wy2 = w2 * thisy;
    float wz2 = w2 * thisz;
    float xx = thisx * thisx;
    float x2 = thisx * 2;
    float xy2 = x2 * thisy;
    float xz2 = x2 * thisz;
    float yy = thisy * thisy;
    float yz2 = 2 * thisy * thisz;
    float zz = thisz * thisz;
    return new Vec3(
        ww * otherx + wy2 * otherz - wz2 * othery +
            xx * otherx + xy2 * othery + xz2 * otherz -
            zz * otherx - yy * otherx,
        xy2 * otherx + yy * othery + yz2 * otherz +
            wz2 * otherx - zz * othery + ww * othery -
            wx2 * otherz - xx * othery,
        xz2 * otherx + yz2 * othery +
            zz * otherz - wy2 * otherx - yy * otherz +
            wx2 * othery - xx * otherz + ww * otherz);
  }

  public static Quaternion identity() {
    return new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
  }


  public Quaternion normalize() {
    Quaternion q1 = this;
    Quaternion q2 = this;
    float dot = getDot(q1, q2);

    float n = (float) (1.0 / Math.sqrt(dot));
    return new Quaternion(x * n, y * n, z * n, w * n);
  }

  private static float getDot(Quaternion q1, Quaternion q2) {
    return (q1.x * q2.x) + (q1.y * q2.y) + (q1.z * q2.z) + (q1.w * q2.w);
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


  /**
   * Interpolates between the specified quaternions and stores the result in
   * the current instance.
   *
   * @param q1 the desired value when interp=0 (not null, unaffected)
   * @param q2 the desired value when interp=1 (not null, may be modified)
   * @param t the fractional change amount
   * @return the (modified) current instance (for chaining)
   * Note: copied from <a href="https://github.com/jMonkeyEngine/jmonkeyengine/blob/master/jme3-core/src/main/java/com/jme3/math/Quaternion.java">JMonkey Engine</a>
   */
  public static Quaternion slerp(Quaternion q1, Quaternion q2, float t) {
    // Create a local quaternion to store the interpolated quaternion
    if (q1.x == q2.x && q1.y == q2.y && q1.z == q2.z && q1.w == q2.w) {
      return q1;
    }

    float result = getDot(q1, q2);

    float q2x = q2.x;
    float q2y = q2.y;
    float q2z = q2.z;
    float q2w = q2.w;
    if (result < 0.0f) {
      // Negate the second quaternion and the result of the dot product
      q2x = -q2.x;
      q2y = -q2.y;
      q2z = -q2.z;
      q2w = -q2.w;
      result = -result;
    }

    // Set the first and second scale for the interpolation
    float scale0 = 1 - t;
    float scale1 = t;

    // Check if the angle between the 2 quaternions was big enough to
    // warrant such calculations
    if ((1 - result) > 0.1f) {// Get the angle between the 2 quaternions,
      // and then store the sin() of that angle
      float theta = (float) Math.acos(result);
      float invSinTheta = (float) (1f / Math.sin(theta));

      // Calculate the scale for q1 and q2, according to the angle and
      // its sine
      scale0 = (float) (Math.sin((1 - t) * theta) * invSinTheta);
      scale1 = (float) (Math.sin((t * theta)) * invSinTheta);
    }

    // Calculate the x, y, z and w values for the quaternion by using a
    // special
    // form of linear interpolation for quaternions.
    float x = (scale0 * q1.x) + (scale1 * q2x);
    float y = (scale0 * q1.y) + (scale1 * q2y);
    float z = (scale0 * q1.z) + (scale1 * q2z);
    float w = (scale0 * q1.w) + (scale1 * q2w);

    // Return the interpolated quaternion
    return new Quaternion(x, y, z, w);
  }

  /**
   * Interpolates quickly between the current instance and {@code q2} using
   * nlerp, and stores the result in the current instance.
   *
   * <p>This method is often faster than
   * {@link #slerp(Quaternion, Quaternion, float)}, but less accurate.
   *
   * @param q2    the desired value when blend=1 (not null, unaffected)
   * @param blend the fractional change amount
   *              Note: copied from <a href="https://github.com/jMonkeyEngine/jmonkeyengine/blob/master/jme3-core/src/main/java/com/jme3/math/Quaternion.java">JMonkey Engine</a>
   * @return
   */
  public static Quaternion nlerp(Quaternion q1, Quaternion q2, float blend) {
    float dot = getDot(q1, q2);
    float blendI = 1.0f - blend;
    if (dot < 0.0f) {
      float x = blendI * q1.x - blend * q2.x;
      float y = blendI * q1.y - blend * q2.y;
      float z = blendI * q1.z - blend * q2.z;
      float w = blendI * q1.w - blend * q2.w;
      return new Quaternion(x,y,z,w).normalize();
    } else {
      float x = blendI * q1.x + blend * q2.x;
      float y = blendI * q1.y + blend * q2.y;
      float z = blendI * q1.z + blend * q2.z;
      float w = blendI * q1.w + blend * q2.w;
      return new Quaternion(x,y,z,w).normalize();
    }
  }

}
