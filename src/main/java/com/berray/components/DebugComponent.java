package com.berray.components;

import com.berray.GameObject;
import com.berray.math.Rect;

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
      Rect area = parent.get("worldArea");
      if (area != null) {
        DrawRectangleLines(
            (int) area.getX(), (int) area.getY(), (int) area.getWidth(), (int) area.getHeight(),
            LIME);
      }
    }
  }

  public static DebugComponent debug() {
    return new DebugComponent();
  }
}
