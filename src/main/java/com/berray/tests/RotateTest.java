package com.berray.tests;

import com.berray.BerrayApplication;
import com.berray.GameObject;
import com.berray.components.core.AnchorType;
import com.berray.components.movement.OrbitComponent;
import com.berray.math.Vec2;
import com.raylib.Jaylib;

import static com.berray.AssetManager.loadSprite;
import static com.berray.components.core.SpriteComponent.sprite;

public class RotateTest extends BerrayApplication {
  @Override
  public void game() {
    loadSprite("berry", "resources/berry.png");

    debug = true;

    GameObject mainNode = add(
        pos(game.center()),
        rotate(0)
    );

    for (int i = 0; i < AnchorType.values().length; i++) {
      int x = i % 3 - 1;
      int y = i / 3 - 1;
      mainNode.add(
          sprite("berry"),
          pos(x * 250, y * 250),
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

    for (int i = 0; i < 10; i++) {
      mainNode.add(
          sprite("berry"),
          pos(0, 0),
          anchor(AnchorType.CENTER),
          rotate(-90+i*15),
          OrbitComponent.orbit(150, 75, i*15),
          "orbiting"
      );
    }

    onUpdate("orbiting", (event) -> {
      GameObject berry = event.getParameter(0);
      float frameTime = event.getParameter(1);
      Float angle = berry.get("angle");
      angle += frameTime * 75;
      berry.set("angle", angle);
    });

    on("mousePress", (event) -> {
      Vec2 pos = event.getParameter(0);
      mainNode.set("pos", pos);
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