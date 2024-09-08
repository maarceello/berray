package com.berray.tests;

import com.berray.BerrayApplication;
import com.berray.components.CoreComponentShortcuts;
import com.berray.components.core.AnchorType;
import com.berray.math.Color;
import com.raylib.Jaylib;

public class LayerTest extends BerrayApplication implements CoreComponentShortcuts {
  @Override
  public void game() {

    debug = true;

    layers(
      "background",
      "default",
      "gui"
    );

    add(
        pos(center()),
        anchor(AnchorType.CENTER),
        rect(100, 100),
        layer("gui"),
        color(0, 0, 255)
    );

    add(
        pos(center()),
        anchor(AnchorType.LEFT),
        rect(200, 200),
        layer("default"),
        color(0, 100, 0)
    );
    add(
        pos(center()),
        anchor(AnchorType.RIGHT),
        rect(200, 200),
        color(0, 200, 0)
    );

    add(
        pos(center()),
        anchor(AnchorType.CENTER),
        rect(width()/2, height()/2),
        layer("background"),
        color(255, 0, 0)
    );
  }

  @Override
  public void initWindow() {
    width(1024);
    height(768);
    background(Color.GRAY);
    title("Layer Test");
  }

  public static void main(String[] args) {
    new LayerTest().runGame();
  }
}
