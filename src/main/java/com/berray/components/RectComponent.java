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
    PosComponent pos = gameObject.getComponent(PosComponent.class);
    RotateComponent rotate = gameObject.getComponent(RotateComponent.class);

    DrawRectanglePro(
        pos != null ? new Jaylib.Rectangle(pos.getPos().getX(), pos.getPos().getY(), width, height) : new Jaylib.Rectangle(0, 0, width, height),
        new Jaylib.Vector2((float) width / 2, (float) height / 2),
        rotate != null ? rotate.getAngle() : 0,
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
