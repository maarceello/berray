package com.berray.components;

import com.berray.GameObject;
import com.berray.math.Rect;
import com.berray.math.Vec2;

import com.raylib.Jaylib.Vector2;
import static com.raylib.Jaylib.*;

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
    Vec2 pos = gameObject.getOrDefault("pos", Vec2.origin());
    AnchorType anchor = gameObject.getOrDefault("anchor", AnchorType.CENTER);

    float anchorX = anchor.getX() * radius;
    float anchorY = anchor.getY() * radius;

    DrawCircleV(
        new Vector2(pos.getX()-anchorX, pos.getY()-anchorY),
        radius,
        WHITE);
  }

  public static CircleComponent circle(float radius) {
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


