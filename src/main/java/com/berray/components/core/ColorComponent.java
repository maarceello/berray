package com.berray.components.core;

import com.berray.GameObject;
import com.berray.math.Color;

public class ColorComponent extends Component {
  private Color color;

  public ColorComponent(int r, int g, int b) {
    super("color");
    this.color = new Color(r, g, b);
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
    return new ColorComponent(r, g, b);
  }
}
