package com.berray.tests.gui;

import com.berray.BerrayApplication;
import com.berray.GameObject;
import com.berray.assets.CoreAssetShortcuts;
import com.berray.components.CoreComponentShortcuts;
import com.berray.components.core.AnchorType;
import com.berray.math.Color;
import com.berray.math.Vec2;


public class SliderTest extends BerrayApplication implements CoreComponentShortcuts, CoreAssetShortcuts {


  @Override
  public void game() {
    Vec2 center = center();

    Slider slider = add(
        new Slider(new Vec2(500, 30), 0, 100),
        pos(center.getX(), center().getY()),
        anchor(AnchorType.CENTER)
    );

    GameObject text = add(
        text("foo"),
        pos(10,10),
        anchor(AnchorType.TOP_LEFT),
        color(Color.WHITE)
    );

    slider.on("propertyChange", (event) -> {
      if (event.getParameter(0).equals("value")) {
        text.set("text", String.format("Value: %.2f", event.<Float>getParameter(2)));
      }
    });

  }


  @Override
  public void initWindow() {
    width(1000);
    height(500);
    background(Color.BLACK);
    title("Slider Test");
  }

  public static void main(String[] args) {
    new SliderTest().runGame();
  }
}
