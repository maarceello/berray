package com.berray.components.core;

import com.berray.GameObject;
import com.berray.math.Color;
import com.berray.math.Vec2;

import static com.raylib.Raylib.*;

/**
 * Provides a circle shape for rendering.
 */
public class CircleComponent extends Component {
  private float radius;
  private boolean fill = true;

  public CircleComponent(float radius) {
    super("circle");
    this.radius = radius;
  }

  @Override
  public void add(GameObject gameObject) {
    registerGetter("size", this::getSize);
    registerGetter("render", () -> true);
    registerBoundProperty("radius", this::getRadius, this::setRadius);
    registerBoundProperty("fill", this::getFill, this::setFill);
  }

  /**
   * sets whether the circle should be filled or just an outline.
   *
   * @type configuration
   */
  public CircleComponent fill(boolean fill) {
    this.fill = fill;
    return this;
  }


  /**
   * returns the radius of the circle
   *
   * @type property
   */
  public float getRadius() {
    return radius;
  }

  /**
   * sets the radius of the circle
   *
   * @type property
   */
  public void setRadius(float radius) {
    this.radius = radius;
    gameObject.setTransformDirty();
  }

  /**
   * returns the rectangular size of the circle
   *
   * @type property
   */
  private Vec2 getSize() {
    return new Vec2(radius * 2, radius * 2);
  }

  /**
   * returns true when the circle should be filled
   *
   * @type property
   */
  public boolean getFill() {
    return fill;
  }

  /**
   * sets whether the circle should be filled or not
   *
   * @type property
   */
  public void setFill(boolean fill) {
    this.fill = fill;
  }


  @Override
  public void draw() {
    rlPushMatrix();
    {
      Color color = gameObject.getOrDefault("color", Color.WHITE);
      rlMultMatrixf(gameObject.getWorldTransform().toFloatTransposed());
      if (fill) {
        DrawCircle((int) radius, (int) radius, radius, color.toRaylibColor());
      } else {
        DrawCircleLines((int) radius, (int) radius, radius, color.toRaylibColor());
      }
    }
    rlPopMatrix();
  }

  /**
   * creates a circle
   *
   * @param radius radius of the circle
   * @type creator
   */
  public static CircleComponent circle(float radius) {
    return new CircleComponent(radius);
  }

}



