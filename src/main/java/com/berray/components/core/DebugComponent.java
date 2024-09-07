package com.berray.components.core;

import com.berray.GameObject;
import com.berray.math.*;
import com.raylib.Jaylib;
import com.raylib.Raylib;

import static com.raylib.Jaylib.*;
import static com.raylib.Raylib.DrawLine;
import static com.raylib.Raylib.DrawText;

public class DebugComponent extends Component {
  public DebugComponent() {
    super("debug");
  }

  @Override
  public void draw() {
    if (gameObject != null) {
      GameObject parent = gameObject.getParent();
      Matrix4 localTransform = gameObject.getLocalTransformWithoutAnchor();
      Matrix4 parentsWorldTransform = parent.getWorldTransform();
      Matrix4 worldTransform = parentsWorldTransform.multiply(localTransform);
      Vec3 pos = worldTransform.multiply(Vec3.origin());
      if (pos != null) {
        AnchorType anchor = gameObject.getOrDefault("anchor", AnchorType.CENTER);
        drawPoint(pos, LIME);
        int id = gameObject.getId();
        DrawText("#" + id, (int) pos.getX(), (int) pos.getY() - 25, 15, GOLD);


        Vec2 size = gameObject.get("size");
        if (size != null) {
          Vec2 anchorPoint = anchor.getAnchorPoint(size);
          float width = size.getX();
          float height = size.getY();
          Vec3 p1 = worldTransform.multiply(anchorPoint.getX(), anchorPoint.getY(), 0);
          Vec3 p2 = worldTransform.multiply(anchorPoint.getX() + width, anchorPoint.getY(), 0);
          Vec3 p3 = worldTransform.multiply(anchorPoint.getX(), anchorPoint.getY() + height, 0);
          Vec3 p4 = worldTransform.multiply(anchorPoint.getX() + width, anchorPoint.getY() + height, 0);

          // draw transformed rectangle around the shape
          drawLine(p1, p2, LIME);
          drawLine(p1, p3, LIME);
          drawLine(p4, p2, LIME);
          drawLine(p4, p3, LIME);

          Rect bb = gameObject.getBoundingBox();
          if (bb != null) {
            Raylib.Color color = GOLD;
            AreaComponent area = gameObject.getComponent(AreaComponent.class);
            if (area != null && area.isColliding()) {
              color = PINK;
            }

            DrawLine((int) bb.getX(), (int) bb.getY(), (int) (bb.getX() + bb.getWidth()), (int) bb.getY(), color);
            DrawLine((int) bb.getX(), (int) (bb.getY() + bb.getHeight()), (int) (bb.getX() + bb.getWidth()), (int) (bb.getY() + bb.getHeight()), color);
            DrawLine((int) bb.getX(), (int) bb.getY(), (int) bb.getX(), (int) (bb.getY() + bb.getHeight()), color);
            DrawLine((int) (bb.getX() + bb.getWidth()), (int) bb.getY(), (int) (bb.getX() + bb.getWidth()), (int) (bb.getY() + bb.getHeight()), color);

            for (Collision collision : area.getCollisions()) {
              Vec2 displacement = collision.getDisplacement();
              DrawLine((int) p1.getX(), (int) p1.getY(), (int) (p1.getX()+displacement.getX()), (int) (p1.getY()+displacement.getY()), GOLD);
            }
          }
        }
      }
    }
  }

  private static void drawPoint(Vec3 pos, Raylib.Color color) {
    DrawCircleLines((int) pos.getX(), (int) pos.getY(), 10, color);
    DrawLine((int) pos.getX() - 10, (int) pos.getY() + 10, (int) pos.getX() + 10, (int) pos.getY() - 10, color);
    DrawLine((int) pos.getX() + 10, (int) pos.getY() + 10, (int) pos.getX() - 10, (int) pos.getY() - 10, color);
  }

  private void drawLine(Vec3 p1, Vec3 p2, Raylib.Color color) {
    DrawLine((int) p1.getX(), (int) p1.getY(), (int) p2.getX(), (int) p2.getY(), color);
//    DrawLineEx(new Jaylib.Vector2(p1.getX(),p1.getY()), new Jaylib.Vector2(p2.getX(), p2.getY()), 10.0f, color);
  }

  public static DebugComponent debug() {
    return new DebugComponent();
  }
}
