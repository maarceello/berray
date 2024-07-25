package com.berray.components.core;

import com.berray.GameObject;
import com.berray.math.Matrix4;
import com.berray.math.Vec2;
import com.berray.math.Vec3;
import com.raylib.Jaylib;
import com.raylib.Raylib;

import static com.raylib.Jaylib.*;

public class DebugComponent extends Component {
  public DebugComponent() {
    super("debug");
  }

  @Override
  public void draw() {
    GameObject parent = gameObject.getParent();
    if (parent != null) {
      GameObject parentsParent = gameObject.getParent().getParent();
      Matrix4 localTransform = gameObject.getParent().getLocalTransformWithoutAnchor();
      Matrix4 parentsWorldTransform = parentsParent.getWorldTransform();
      Matrix4 worldTransform = parentsWorldTransform.multiply(localTransform);
      Vec3 pos = worldTransform.multiply(Vec3.center());
      if (pos != null) {
        AnchorType anchor = parent.getOrDefault("anchor", AnchorType.CENTER);
        drawPoint(pos, LIME);
        int id = gameObject.getParent().getId();
        Jaylib.DrawText("#" + id, (int) pos.getX(), (int) pos.getY() - 25, 15, GOLD);

        Vec2 size = parent.get("size");
        if (size != null) {
          Vec2 anchorPoint = anchor.getAnchorPoint(size);
          float width = size.getX();
          float height = size.getY();
          Vec3 p1 = worldTransform.multiply(anchorPoint.getX(), anchorPoint.getY(), 0);
          Vec3 p2 = worldTransform.multiply(anchorPoint.getX() + width, anchorPoint.getY(), 0);
          Vec3 p3 = worldTransform.multiply(anchorPoint.getX(), anchorPoint.getY() + height, 0);
          Vec3 p4 = worldTransform.multiply(anchorPoint.getX() + width, anchorPoint.getY() + height, 0);

          int x1 = (int) Math.min(p1.getX(), Math.min(p2.getX(), Math.min(p3.getX(), p4.getX())));
          int x2 = (int) Math.max(p1.getX(), Math.max(p2.getX(), Math.max(p3.getX(), p4.getX())));

          int y1 = (int) Math.min(p1.getY(), Math.min(p2.getY(), Math.min(p3.getY(), p4.getY())));
          int y2 = (int) Math.max(p1.getY(), Math.max(p2.getY(), Math.max(p3.getY(), p4.getY())));

          drawLine(p1, p2, LIME);
          drawLine(p1, p3, LIME);
          drawLine(p4, p2, LIME);
          drawLine(p4, p3, LIME);

          Jaylib.DrawLine(x1, y1, x2, y1, GOLD);
          Jaylib.DrawLine(x1, y1, x1, y2, GOLD);
          Jaylib.DrawLine(x1, y2, x2, y2, GOLD);
          Jaylib.DrawLine(x2, y1, x2, y2, GOLD);
        }
      }
    }
  }

  private static void drawPoint(Vec3 pos, Raylib.Color color) {
    DrawCircleLines((int) pos.getX(), (int) pos.getY(), 10, color);
    Jaylib.DrawLine((int) pos.getX() - 10, (int) pos.getY() + 10, (int) pos.getX() + 10, (int) pos.getY() - 10, color);
    Jaylib.DrawLine((int) pos.getX() + 10, (int) pos.getY() + 10, (int) pos.getX() - 10, (int) pos.getY() - 10, color);
  }

  private void drawLine(Vec3 p1, Vec3 p2, Raylib.Color color) {
    Jaylib.DrawLine((int) p1.getX(), (int) p1.getY(), (int) p2.getX(), (int) p2.getY(), color);
  }

  public static DebugComponent debug() {
    return new DebugComponent();
  }
}
