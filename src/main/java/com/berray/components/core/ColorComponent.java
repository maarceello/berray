package com.berray.components.core;

import com.berray.GameObject;
import com.berray.math.Color;

/**
 * Component to supply color to other drawing components.
 */
public class ColorComponent extends Component {
  private Color color;

  public ColorComponent(Color color) {
    super("color");
    this.color = color;
  }

  @Override
  public void add(GameObject gameObject) {
    registerBoundProperty("color", this::getColor, this::setColor);
  }

  /**
   * returns the color
   *
   * @type property
   */
  public Color getColor() {
    return color;
  }


  /**
   * sets the color
   *
   * @type property
   */
  private void setColor(Color color) {
    this.color = color;
  }

  /**
   * creates a color component from red, green and blue color components
   *
   * @type creator
   */
  public static ColorComponent color(int r, int g, int b) {
    return new ColorComponent(new Color(r / 255.0f, g / 255.0f, b / 255.0f));
  }

  /**
   * creates a color component from a color
   *
   * @type creator
   */
  public static ColorComponent color(Color color) {
    return new ColorComponent(color);
  }
}
