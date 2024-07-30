package com.berray.tests;

import com.berray.BerrayApplication;
import com.berray.components.CoreComponentShortcuts;
import com.berray.components.core.AnchorType;
import com.raylib.Jaylib;

public class CollisionTest extends BerrayApplication implements CoreComponentShortcuts {
  @Override
  public void game() {
    debug = true;

    add(
        rect(100, 100),
        area(),
        pos(100, 100),
        anchor(AnchorType.TOP_LEFT)
    );

    add(
        rect(100, 100),
        area(),
        pos(150, 150),
        anchor(AnchorType.TOP_LEFT)
    );
  }

  @Override
  public void initWindow() {
    width(1024);
    height(768);
    background(Jaylib.BLACK);
    title("Anchor Test");
  }

  public static void main(String[] args) {
    new CollisionTest().runGame();
  }

}
