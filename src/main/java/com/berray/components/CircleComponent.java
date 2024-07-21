package com.berray.components;

import com.berray.GameObject;
import com.berray.math.Rect;
import com.berray.math.Vec2;

import com.raylib.Jaylib.Vector2;
import static com.raylib.Jaylib.*;

public class CircleComponent extends Component {
  private final int radius;

  public CircleComponent(int radius) {
    super("circle");
    this.radius = radius;
  }

  public int getRadius() {
    return radius;
  }

  @Override
  public void draw() {

    PosComponent pos = gameObject.getComponent(PosComponent.class);

    DrawCircleV(
        pos != null ? pos.getPos().toVecor2() : new Vector2(0, 0),
        radius,
        WHITE);
  }

  public static CircleComponent circle(int radius) {
    return new CircleComponent(radius);
  }

  @Override
  public void add(GameObject gameObject) {
    gameObject.registerGetter("localArea", this::localArea);
  }

  private Rect localArea() {
    Vec2 pos = gameObject.get("pos");
    if (pos == null) {
      return null;
    }
    return new Rect(pos.getX() - radius, pos.getY() - radius, radius*2, radius*2);
  }

}



