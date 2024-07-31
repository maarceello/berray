package com.berray.components.core;

import com.berray.GameObject;
import com.berray.math.Rect;
import com.berray.math.Vec2;

import com.raylib.Raylib;

import static com.raylib.Jaylib.*;
import static com.raylib.Raylib.*;

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
    rlPushMatrix();
    {
      Raylib.Color color = gameObject.getOrDefault("color", WHITE);
      rlMultMatrixf(gameObject.getWorldTransform().toFloatTransposed());
      DrawRectangle(0,0, (int) width, (int) height, color);
    }
    rlPopMatrix();
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
    return new Rect(pos.getX() - width / 2, pos.getY() - height / 2, width, height);
  }

  private Vec2 getSize() {
    return new Vec2(width, height);
  }

  public static RectComponent rect(float width, float height) {
    return new RectComponent(width, height);
  }

}
