package com.berray.components;

import com.berray.GameObject;

import static com.raylib.Jaylib.Vector2;
import static com.raylib.Jaylib.Rectangle;
import static com.raylib.Jaylib.DrawRectanglePro;
import static com.raylib.Jaylib.WHITE;

public class RectComponent extends Component {
  private final int width;
  private final int height;

  public RectComponent(int width, int height) {
    this.width = width;
    this.height = height;
  }

  @Override
  public void draw(GameObject gameObject) {
    PosComponent pos = gameObject.getComponent(PosComponent.class);
    RotateComponent rotate = gameObject.getComponent(RotateComponent.class);

    DrawRectanglePro(
        pos != null ? new Rectangle(pos.getPos().x(), pos.getPos().y(), width, height) : new Rectangle(0, 0, width, height),
        new Vector2((float) width / 2, (float) height / 2),
        rotate != null ? rotate.getAngle() : 0,
        WHITE);

  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public static RectComponent rect(int width, int height) {
    return new RectComponent(width, height);
  }
}
