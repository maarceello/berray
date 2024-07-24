package com.berray.components.core;

import com.berray.GameObject;
import com.berray.math.Rect;
import com.berray.math.Vec2;

import static com.raylib.Jaylib.*;
import static com.raylib.Raylib.DrawRectangleLines;

public class DebugComponent extends Component {
  public DebugComponent() {
    super("debug");
  }

  @Override
  public void draw() {
    GameObject parent = gameObject.getParent();
    if (parent != null) {
      Vec2 pos = parent.get("pos");
      if (pos != null) {
        DrawCircleLines((int) pos.getX(), (int) pos.getY(), 10, LIME);
        DrawLine((int) pos.getX() - 10, (int) pos.getY() + 10, (int) pos.getX() + 10, (int) pos.getY() - 10, LIME);
        DrawLine((int) pos.getX() + 10, (int) pos.getY() + 10, (int) pos.getX() - 10, (int) pos.getY() - 10, LIME);
        Rect area = parent.get("localArea");
        if (area != null) {
          DrawRectangleLines(
              (int) area.getX(), (int) area.getY(), (int) area.getWidth(), (int) area.getHeight(),
              LIME);
        }
      }
    }
  }

  public static DebugComponent debug() {
    return new DebugComponent();
  }
}
