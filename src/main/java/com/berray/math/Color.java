package com.berray.math;

import com.raylib.Jaylib;
import com.raylib.Raylib;

public class Color {
  public static final Color GOLD = new Color(1.0f, 0.8f, 0.0f);
  public static final Color WHITE = new Color(1.0f, 1.0f, 1.0f);
  public static final Color BLACK = new Color(0.0f, 0.0f, 0.0f);
  public static final Color GRAY = new Color(0.5f, 0.5f, 0.5f);
  public static final Color RED = new Color(1.0f, 0.0f, 0.0f);

  private float r;
  private float g;
  private float b;
  private float a;

  private Raylib.Color raylibColor = null;

  public Color() {
  }

  public Color(float r, float g, float b) {
    this.r = r;
    this.g = g;
    this.b = b;
    this.a = 1.0f;
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

  public Raylib.Color toRaylibColor() {
    if (raylibColor == null) {
      raylibColor =  new Jaylib.Color((int) (r * 255), (int) (g * 255), (int) (b * 255), (int) (a * 255));
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
