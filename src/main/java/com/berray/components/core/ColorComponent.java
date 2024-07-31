package com.berray.components.core;

import com.berray.GameObject;
import com.raylib.Jaylib;
import com.raylib.Raylib;

public class ColorComponent extends Component {
  private final Raylib.Color color;
  public ColorComponent(int r, int g, int b) {
    super("color");
    this.color = new Jaylib.Color(r,g,b, 255);
  }

  public Raylib.Color getColor() {
    return color;
  }

  @Override
  public void add(GameObject gameObject) {
    registerGetter("color", this::getColor);
  }

  public static ColorComponent color(int r, int g, int b) {
    return new ColorComponent(r, g, b);
  }
}
