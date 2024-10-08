package com.berray.tests;

import com.berray.BerrayApplication;
import com.berray.assets.CoreAssetShortcuts;
import com.berray.components.CoreComponentShortcuts;
import com.berray.components.core.AnchorType;
import com.berray.math.Color;
import com.berray.math.Vec2;
import com.raylib.Jaylib;
import com.raylib.Raylib;

import static com.berray.objects.core.Label.label;

public class AnchorTest extends BerrayApplication implements CoreComponentShortcuts, CoreAssetShortcuts {
  @Override
  public void game() {
    loadSprite("berry", "resources/berry.png");

    debug = true;

    add(
        label(() -> "FPS: "+ Raylib.GetFPS()),
        pos(Vec2.origin()),
        anchor(AnchorType.TOP_LEFT),
        color(255, 0, 0)
    );

    for (int i = 0; i < AnchorType.values().length; i++) {
      int xgaps = 100;
      add(
          rect(20, 40),
          pos(50+i* xgaps, 50),
          anchor(AnchorType.values()[i])
      );
      add(
          circle(20),
          pos(50+i* xgaps, 150),
          anchor(AnchorType.values()[i])
      );

      add(
          sprite("berry"),
          pos(50+i* xgaps, 300),
          anchor(AnchorType.values()[i])
      );
      add(
          text("berry"),
          color(Color.WHITE),
          pos(50+i* xgaps, 400),
          anchor(AnchorType.values()[i])
      );
    }

  }

  @Override
  public void initWindow() {
    width(1024);
    height(768);
    background(Color.BLACK);
    title("Anchor Test");
  }

  public static void main(String[] args) {
    new AnchorTest().runGame();
  }

}
