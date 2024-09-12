package com.berray.components.core;

import com.berray.GameObject;
import com.berray.math.Color;

public class ColorComponent extends Component {
  private Color color;

  public ColorComponent(Color color) {
    super("color");
    this.color = color;
  }

  public Color getColor() {
    return color;
  }

  @Override
  public void add(GameObject gameObject) {
    registerBoundProperty("color", this::getColor, this::setColor);
  }

  private void setColor(Color color) {
    this.color = color;
  }

  public static ColorComponent color(int r, int g, int b) {
    return new ColorComponent(new Color(r / 255.0f, g / 255.0f, b / 255.0f));
  }

  public static ColorComponent color(Color color) {
    return new ColorComponent(color);
  }
}
