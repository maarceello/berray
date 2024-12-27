package com.berray.math;

public class MathUtil {

  private MathUtil() {
  }

  public static float clamp(float value, float minInclusive, float maxInclusive) {
    return Math.max(minInclusive, Math.min(maxInclusive, value));
  }
}
