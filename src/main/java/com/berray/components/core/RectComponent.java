package com.berray.components.core;

import com.berray.GameObject;
import com.berray.math.Color;
import com.berray.math.Vec2;
import com.raylib.Jaylib;

import static com.raylib.Jaylib.*;

/** Rectangular drawn shape. */
public class RectComponent extends Component {
  private Vec2 size;
  private boolean fill = true;
  private float lineThickness = 1.0f;

  public RectComponent(float width, float height) {
    super("rect");
    this.size = new Vec2(width, height);
  }


  @Override
  public void add(GameObject gameObject) {
    super.add(gameObject);
    registerBoundProperty("size", this::getSize, this::setSize);
    registerBoundProperty("fill", this::getFill, this::setFill);
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
    rlPushMatrix();
    {
      Color color = gameObject.getOrDefault("color", Color.WHITE);
      rlMultMatrixf(gameObject.getWorldTransform().toFloatTransposed());
      if (fill) {
        DrawRectangle(0, 0, (int) size.getX(), (int) size.getY(), color.toRaylibColor());
      } else {
        DrawRectangleLinesEx(new Jaylib.Rectangle(0, 0, (int) size.getX(), (int) size.getY()),lineThickness, color.toRaylibColor());
      }
    }
    rlPopMatrix();
  }

  /**
   * creates a new rect component
   *
   * @param width  width of the rectangle
   * @param height height of the rectangle
   * @type creator
   */
  public static RectComponent rect(float width, float height) {
    return new RectComponent(width, height);
  }

}
