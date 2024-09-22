package com.berray.components.core;

import com.berray.GameObject;
import com.berray.math.Color;
import com.berray.math.Vec2;

import static com.raylib.Jaylib.*;

public class RectComponent extends Component {
  private Vec2 size;
  private boolean fill = true;

  public RectComponent(float width, float height) {
    super("rect");
    this.size = new Vec2(width, height);
  }

  public RectComponent fill(boolean fill) {
    this.fill = fill;
    return this;
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
        DrawRectangleLines(0, 0, (int) size.getX(), (int) size.getY(), color.toRaylibColor());
      }
    }
    rlPopMatrix();
  }


  @Override
  public void add(GameObject gameObject) {
    super.add(gameObject);
    registerBoundProperty("size", this::getSize, this::setSize);
    registerGetter("render", () -> true);
  }

  private Vec2 getSize() {
    return size;
  }

  public void setSize(Vec2 size) {
    this.size = size;
  }

  public static RectComponent rect(float width, float height) {
    return new RectComponent(width, height);
  }

}
