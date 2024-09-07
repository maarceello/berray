package com.berray.tests.gui;

import com.berray.BerrayApplication;
import com.berray.GameObject;
import com.berray.components.CoreComponentShortcuts;
import com.berray.components.core.AnchorType;
import com.berray.math.Vec2;
import com.raylib.Jaylib;

public class MouseCalculationTest extends BerrayApplication implements CoreComponentShortcuts {

  private float angle = 0;

  @Override
  public void game() {

    targetFps = 1000;

    Vec2 center = center();
    GameObject root = add(
        rotate(0),
        pos(center)
    );

    addFpsLabel();
    addTimingsLabel();

    for (AnchorType type : AnchorType.values()) {
      int x = type.ordinal() % 3;
      int y = type.ordinal() / 3;

      GameObject object = root.add(
          rect(300, 300),
          color(128, 128, 128),
          pos(20 + x * 470 - center.getX(), 20 + y * 470 - center.getY()),
          area(),
          anchor(type),
          mouse()
      );
      object.registerPropertyGetter("static", () -> true);

      for (AnchorType type2 : AnchorType.values()) {
        int x2 = type2.ordinal() % 3;
        int y2 = type2.ordinal() / 3;

        GameObject inner = object.add(
            rect(80, 80),
            color(64, 64, 64),
            pos(10 + x2 * (300 - 20) / 2, 10 + y2 * (300 - 20) / 2),
            area(),
            anchor(type2),
            mouse()
        );
        inner.registerPropertyGetter("static", () -> true);
      }
    }

    root.on("update", event -> {
      float deltaTime = event.getParameter(0);
      angle += deltaTime * 30f;
      root.set("angle", angle);
    });


  }

  @Override
  public void initWindow() {
    width(1000);
    height(1000);
    background(Jaylib.GRAY);
    title("Databinding Test");
  }

  public static void main(String[] args) {
    new MouseCalculationTest().runGame();
  }
}
