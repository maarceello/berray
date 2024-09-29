package com.berray.components.core;

import com.berray.GameObject;
import com.berray.math.Vec2;
import com.berray.math.Color;

import static com.raylib.Raylib.*;

/**
 * # CircleComponent
 *
 * {@link CircleComponent#circle(float)} provides a circle shape for rendering.
 *
 * # Properties
 *
 * - localArea (read only) - todo
 * - size (read only) - size of the circle (2 * radius)
 *
 * # Events
 *
 * none
 */
public class CircleComponent extends Component {
  private float radius;

  public CircleComponent(float radius) {
    super("circle");
    this.radius = radius;
  }

  public float getRadius() {
    return radius;
  }

  public void setRadius(float radius) {
    this.radius = radius;
    gameObject.setTransformDirty();
  }

  @Override
  public void draw() {
    rlPushMatrix();
    {
      Color color = gameObject.getOrDefault("color", Color.WHITE);
      rlMultMatrixf(gameObject.getWorldTransform().toFloatTransposed());
      DrawCircle((int) radius, (int) radius, radius, color.toRaylibColor());
    }
    rlPopMatrix();
  }


  @Override
  public void add(GameObject gameObject) {
    registerGetter("size", this::getSize);
    registerGetter("render", () -> true);
    registerBoundProperty("radius", this::getRadius, this::setRadius);
  }

  private Vec2 getSize() {
    return new Vec2(radius * 2, radius * 2);
  }

  public static CircleComponent circle(float radius) {
    return new CircleComponent(radius);
  }

}



