package com.berray.components;

import com.berray.GameObject;

import static com.raylib.Jaylib.Vector2;
import static com.raylib.Jaylib.DrawCircleV;
import static com.raylib.Jaylib.WHITE;

public class CircleComponent extends Component {
  private final int radius;

  public CircleComponent(int radius) {
    this.radius = radius;
  }

  public int getRadius() {
    return radius;
  }

  @Override
  public void draw(GameObject gameObject) {

    PosComponent pos = gameObject.getComponent(PosComponent.class);

    DrawCircleV(
        pos != null ? pos.getPos() : new Vector2(0, 0),
        radius,
        WHITE);



  }

  public static CircleComponent circle(int radius) {
    return new CircleComponent(radius);
  }
}



