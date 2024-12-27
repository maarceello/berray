package com.berray.math;

import com.raylib.Jaylib;
import com.raylib.Raylib;

public class Color {
  public static final Color GOLD = new Color(1.0f, 0.8f, 0.0f);
  public static final Color WHITE = new Color(1.0f, 1.0f, 1.0f);
  public static final Color BLACK = new Color(0.0f, 0.0f, 0.0f);
  public static final Color GRAY = new Color(0.5f, 0.5f, 0.5f);
  public static final Color RED = new Color(1.0f, 0.0f, 0.0f);
  public static final Color GREEN = new Color(0.0f, 1.0f, 0.0f);

  private float r;
  private float g;
  private float b;
  private float a;

  private Raylib.Color raylibColor = null;

  public Color() {
    this(0.0f, 0.0f, 0.0f, 1.0f);
  }

  public Color(float r, float g, float b) {
    this(r, g, b, 1.0f);
  }

  public Color(float r, float g, float b, float a) {
    this.r = r;
    this.g = g;
    this.b = b;
    this.a = a;
  }

  public static Color linearInterpolate(Color first, Color second, float ratio) {
    float r = first.r + (second.r - first.r) * ratio;
    float g = first.g + (second.g - first.g) * ratio;
    float b = first.b + (second.b - first.b) * ratio;
    float a = first.a + (second.a - first.a) * ratio;
    return new Color(r, g, b, a);
  }

  public float getR() {
    return r;
  }

  public void setR(float r) {
    this.r = r;
  }

  public float getG() {
    return g;
  }

  public void setG(float g) {
    this.g = g;
  }

  public float getB() {
    return b;
  }

  public void setB(float b) {
    this.b = b;
  }

  public float getA() {
    return a;
  }

  public void setA(float a) {
    this.a = a;
  }

  public Color brighter(float factor) {
    // if we're black...return gray
    if ( r == 0 && g == 0 && b == 0) {
      return new Color(factor, factor, factor, a);
    }

    return new Color(
        Math.min((r/factor), 1.0f),
        Math.min((g/factor), 1.0f),
        Math.min((b/factor), 1.0f),
        a);
  }

  public Color darker(float factor) {
    return new Color(r * factor, g * factor, b * factor, a);
  }



  public Raylib.Color toRaylibColor() {
    if (raylibColor == null) {
      raylibColor = new Jaylib.Color((int) (r * 255), (int) (g * 255), (int) (b * 255), (int) (a * 255));
    }
    return raylibColor;
  }

  @Override
  public String toString() {
    return "Color{" +
        "r=" + r +
        ", g=" + g +
        ", b=" + b +
        ", a=" + a +
        '}';
  }
}
