package com.berray.tests.gui;

import com.berray.BerrayApplication;
import com.berray.GameObject;
import com.berray.components.CoreComponentShortcuts;
import com.berray.components.core.AnchorType;
import com.berray.math.Color;
import com.raylib.Jaylib;

import static com.berray.tests.gui.Button.button;

public class SliderTest extends BerrayApplication implements CoreComponentShortcuts {
  @Override
  public void game() {

    Button button1 = (Button) add(
        button("testbutton hover"),
        area(),
        rect(100, 100),
        color(255, 255, 255),
        mouse(),
        pos(center()),
        anchor(AnchorType.LEFT)
    );

    Button button2 = (Button) add(
        button("testbutton"),
        area(),
        rect(100, 100),
        color(255, 255, 255),
        mouse(),
        pos(center()),
        anchor(AnchorType.RIGHT)
    );


    button1.on("hoverEnter", event -> {
      ((GameObject) event.getParameter(0)).set("color", Color.GOLD);
    });
    button1.on("hoverLeave", event -> {
      ((GameObject) event.getParameter(0)).set("color", Color.WHITE);
    });
    button2.on("mousePress", event -> {
      ((GameObject) event.getParameter(0)).set("color", Color.BLACK);
    });
    button2.on("mouseRelease", event -> {
      ((GameObject) event.getParameter(0)).set("color", Color.GOLD);
    });


  }

  @Override
  public void initWindow() {
    width(500);
    height(500);
    background(Jaylib.GRAY);
    title("Databinding Test");
  }

  public static void main(String[] args) {
    new SliderTest().runGame();
  }
}
