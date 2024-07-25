package com.berray.components.core;

import com.berray.GameObject;
import com.berray.math.Rect;
import com.berray.math.Vec2;
import com.raylib.Raylib;

import static com.raylib.Jaylib.WHITE;
import static com.raylib.Raylib.DrawCircle;

public class CircleComponent extends Component {
  private final float radius;

  public CircleComponent(float radius) {
    super("circle");
    this.radius = radius;
  }

  public float getRadius() {
    return radius;
  }

  @Override
  public void draw() {
    Raylib.rlPushMatrix();
    {
      Raylib.rlMultMatrixf(gameObject.getWorldTransform().toFloatTransposed());
      DrawCircle((int) (radius), (int) (radius), radius, WHITE);
    }
    Raylib.rlPopMatrix();
  }


  @Override
  public void add(GameObject gameObject) {
    gameObject.registerGetter("localArea", this::localArea);
    gameObject.registerGetter("size", this::getSize);
  }

  private Rect localArea() {
    Vec2 pos = gameObject.get("pos");
    if (pos == null) {
      return null;
    }
    return new Rect(pos.getX() - radius, pos.getY() - radius, radius * 2, radius * 2);
  }

  private Vec2 getSize() {
    return new Vec2(radius * 2, radius * 2);
  }

  public static CircleComponent circle(float radius) {
    return new CircleComponent(radius);
  }

}



