package com.berray.components.core;

import com.berray.GameObject;
import com.berray.math.Color;
import com.berray.math.Vec2;
import com.raylib.Raylib;

/**
 * Rectangular drawn shape.
 */
public class RectComponent extends Component {
  private Vec2 size;
  private boolean fill = true;
  private float lineThickness = 1.0f;
  private int radius;

  public RectComponent(Vec2 size) {
    super("rect");
    this.size = size;
  }


  @Override
  public void add(GameObject gameObject) {
    super.add(gameObject);
    registerBoundProperty("size", this::getSize, this::setSize);
    registerBoundProperty("fill", this::getFill, this::setFill);
    registerBoundProperty("radius", this::getRadius, this::setRadius);
    registerBoundProperty("lineThickness", this::getLineThickness, this::setLineThickness);
    registerGetter("render", () -> true);
  }

  /**
   * sets whether the rectangle should be drawn filled or as a wireframe.
   *
   * @type configuration
   */
  public RectComponent fill(boolean fill) {
    this.fill = fill;
    return this;
  }

  /**
   * Sets whether the radius of the corners. Values <= 1 means no round rectangle.
   *
   * @type configuration
   */
  public RectComponent radius(int radius) {
    this.radius = radius;
    return this;
  }

  /**
   * sets whether the rectangle should be drawn filled or as a wireframe.
   *
   * @type configuration
   */
  public RectComponent lineThickness(float lineThickness) {
    this.lineThickness = lineThickness;
    return this;
  }

  /**
   * Returns the size of the rectangle
   *
   * @type property
   */
  private Vec2 getSize() {
    return size;
  }

  /**
   * Sets the size of the rectangle. Note that this forces recalculation of the transformation matrix.
   *
   * @type property
   */
  public void setSize(Vec2 size) {
    this.size = size;
    gameObject.setTransformDirty();
  }


  /**
   * Returns whether the rectangle should be filled or not.
   *
   * @type property
   */
  public boolean getFill() {
    return fill;
  }

  /**
   * Sets whether the rectangle should be filled or not.
   *
   * @type property
   */
  public void setFill(boolean fill) {
    this.fill = fill;
  }

  /**
   * Returns the radius of the corners.
   *
   * @type property
   */
  public int getRadius() {
    return radius;
  }

  /**
   * Sets the radius of the corners.
   *
   * @type property
   */
  public void setRadius(int radius) {
    this.radius = radius;
  }

  /**
   * Returns the thickness of the drawn line.
   *
   * @type property
   */
  public float getLineThickness() {
    return lineThickness;
  }

  /**
   * Sets the thickness of the drawn line. Default is 1.0;
   *
   * @type property
   */
  public void setLineThickness(float lineThickness) {
    this.lineThickness = lineThickness;
  }

  @Override
  public void draw() {
    Color color = gameObject.getOrDefault("color", Color.WHITE);
    if (fill) {
      Raylib.DrawRectangle(0, 0, (int) size.getX(), (int) size.getY(), color.toRaylibColor());
    } else {
      Raylib.DrawRectangleLinesEx(new Raylib.Rectangle().x(0).y(0).width(size.getX()).height(size.getY()), lineThickness, color.toRaylibColor());
    }
  }

  /**
   * creates a new rect component
   *
   * @param width  width of the rectangle
   * @param height height of the rectangle
   * @type creator
   */
  public static RectComponent rect(float width, float height) {
    return rect(new Vec2(width, height));
  }

  /**
   * creates a new rect component
   *
   * @param size size the rectangle
   * @type creator
   */
  public static RectComponent rect(Vec2 size) {
    return new RectComponent(size);
  }
}
