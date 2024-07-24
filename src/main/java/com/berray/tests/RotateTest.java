package com.berray.tests;

import com.berray.BerrayApplication;
import com.berray.GameObject;
import com.berray.components.AnchorType;
import com.raylib.Jaylib;

import static com.berray.AssetManager.loadSprite;
import static com.berray.components.SpriteComponent.sprite;

public class RotateTest  extends BerrayApplication {
  @Override
  public void game() {
    loadSprite("berry", "resources/berry.png");

    debug = true;

    for (int i = 0; i < AnchorType.values().length; i++) {
      int x = i % 3;
      int y = i / 3;
      add(
          sprite("berry"),
          pos(150+x* 200, 150 + y * 200),
          anchor(AnchorType.values()[i]),
          rotate((360f / 9f) * i),
          "berry"
      );
    }

    onUpdate("berry", (event) -> {
      GameObject berry = event.getParameter(0);
      float frameTime = event.getParameter(1);
      Float angle = berry.get("angle");
      angle += frameTime * 45f;
      berry.set("angle", angle);
    });

  }

  @Override
  public void initWindow() {
    width(1024);
    height(768);
    background(Jaylib.BLACK);
    title("Anchor Test");
  }

  public static void main(String[] args) {
    new RotateTest().runGame();
  }

}
