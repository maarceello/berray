package com.berray.tests;

import com.berray.BerrayApplication;
import com.berray.GameObject;
import com.berray.components.CoreComponentShortcuts;
import com.berray.components.core.AnchorType;
import com.berray.math.Color;
import com.berray.math.Vec2;
import com.raylib.Jaylib;
import com.raylib.Raylib;

public class CollisionTest extends BerrayApplication implements CoreComponentShortcuts {
  @Override
  public void game() {
    debug = true;

    add(
        rect(100, 100),
        area(),
        pos((1024 - 100) / 2.0f, (768 - 100) / 2.0f),
        anchor(AnchorType.TOP_LEFT),
        color(130, 130, 130)
    );

    GameObject second = add(
        rect(100, 100),
        area(),
        pos(150, 150),
        anchor(AnchorType.TOP_LEFT)
    );

    on("update", (event) -> {
      second.set("pos", new Vec2(Raylib.GetMouseX(), Raylib.GetMouseY()));
    });


  }

  @Override
  public void initWindow() {
    width(1024);
    height(768);
    background(Color.BLACK);
    title("Anchor Test");
  }

  public static void main(String[] args) {
    new CollisionTest().runGame();
  }

}
