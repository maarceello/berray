package com.berray.components;

import com.berray.GameObject;
import com.berray.math.Rect;
import com.berray.math.Vec2;
import com.raylib.Jaylib;

import static com.raylib.Jaylib.DrawRectanglePro;
import static com.raylib.Jaylib.WHITE;

public class RectComponent extends Component {
  private final float width;
  private final float height;

  public RectComponent(float width, float height) {
    super("rect");
    this.width = width;
    this.height = height;
  }

  @Override
  public void draw() {
    Vec2 pos = gameObject.getOrDefault("pos", Vec2.origin());
    Float angle = gameObject.getOrDefault("angle", 0f);
    AnchorType anchor = gameObject.getOrDefault("anchor", AnchorType.CENTER);

    float w2 = width / 2;
    float h2 = height / 2;

    float anchorX = w2 + anchor.getX() * w2;
    float anchorY = h2 + anchor.getY() * h2;

    DrawRectanglePro(
        new Jaylib.Rectangle(pos.getX(), pos.getY(), width, height),
        new Jaylib.Vector2(anchorX, anchorY),
        angle,
        WHITE);

  }

  public static RectComponent rect(float width, float height) {
    return new RectComponent(width, height);
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
    return new Rect(pos.getX() - width / 2, pos.getY() - height / 2, width, height);
  }
}
