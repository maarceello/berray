package com.berray.math;

public class Insets {
  public static final Insets NONE = new Insets(0, 0, 0, 0);

  private final float top;
  private final float left;
  private final float bottom;
  private final float right;

  public Insets(float top, float left, float bottom, float right) {
    this.top = top;
    this.left = left;
    this.bottom = bottom;
    this.right = right;
  }

  public float getTop() {
    return top;
  }

  public float getLeft() {
    return left;
  }

  public float getBottom() {
    return bottom;
  }

  public float getRight() {
    return right;
  }

  public Insets add(Insets other) {
    return new Insets(
        top + other.top,
        left + other.left,
        bottom + other.bottom,
        right + other.right);
  }
}
