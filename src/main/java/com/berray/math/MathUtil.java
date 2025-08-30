package com.berray.math;

public class MathUtil {

  private MathUtil() {
  }

  public static float clamp(float value, float minInclusive, float maxInclusive) {
    return Math.max(minInclusive, Math.min(maxInclusive, value));
  }

  /** float  version of {@link Math#sin(double)} */
  public static float sin(float angle) {
    return (float) Math.sin(angle);
  }

  /** float  version of {@link Math#cos(double)} */
  public static float cos(float angle) {
    return (float) Math.cos(angle);
  }

  /** float  version of {@link Math#toRadians(double)} */
  public static float toRadians(float angle) {
    return (float) Math.toRadians(angle);
  }

  /** float  version of {@link Math#toDegrees(double)} */
  public static float toDegrees(float angle) {
    return (float) Math.toDegrees(angle);
  }
}
