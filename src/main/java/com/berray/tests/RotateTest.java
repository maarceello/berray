package com.berray.tests;

import com.berray.BerrayApplication;
import com.berray.GameObject;
import com.berray.assets.CoreAssetShortcuts;
import com.berray.components.CoreComponentShortcuts;
import com.berray.components.core.AnchorType;
import com.berray.event.CoreEvents;
import com.berray.event.MouseEvent;
import com.berray.event.UpdateEvent;
import com.berray.math.Color;
import com.berray.math.Vec2;
import com.raylib.Raylib;

import static com.berray.components.addon.OrbitComponent.orbit;
import static com.berray.objects.core.Label.label;

public class RotateTest extends BerrayApplication implements CoreComponentShortcuts, CoreAssetShortcuts {
  @Override
  public void game() {
    loadSprite("berry", "resources/berry.png");

    debug = true;

    add(
        label(() -> "FPS: " + Raylib.GetFPS()),
        pos(Vec2.origin()),
        anchor(AnchorType.TOP_LEFT),
        color(255, 255, 255)
    );

    GameObject mainNode = add(
        pos(game.center()),
        rotate(0)
    );

    for (int i = 0; i < AnchorType.values().length; i++) {
      int x = i % 3 - 1;
      int y = i / 3 - 1;
      mainNode.add(
          sprite("berry"),
          area(),
          pos(x * 250, y * 250),
          anchor(AnchorType.values()[i]),
          rotate((360f / 9f) * i),
          "berry"
      );
    }

    onUpdate("berry", (UpdateEvent event) -> {
      GameObject berry = event.getSource();
      float frameTime = event.getFrametime();
      Float angle = berry.get("angle");
      angle += frameTime * 45f;
      berry.set("angle", angle);
    });

    for (int i = 0; i < 10; i++) {
      mainNode.add(
          sprite("berry"),
          pos(0, 0),
          area(),
          anchor(AnchorType.CENTER),
          rotate(-90 + i * 15),
          orbit(250, 75, i * 15),
          "orbiting"
      );
    }

    onUpdate("orbiting", (UpdateEvent event) -> {
      GameObject berry = event.getSource();
      float frameTime = event.getFrametime();
      Float angle = berry.get("angle");
      angle += frameTime * 75;
      berry.set("angle", angle);
    });

    on(CoreEvents.MOUSE_PRESS, (MouseEvent event) -> {
      Vec2 pos = event.getWindowPos();
      mainNode.set("pos", pos);
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
    new RotateTest().runGame();
  }

}
