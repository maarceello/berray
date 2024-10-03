package com.berray.tests;

import com.berray.BerrayApplication;
import com.berray.GameObject;
import com.berray.components.CoreComponentShortcuts;
import com.berray.components.core.AnchorType;
import com.berray.math.Color;
import com.raylib.Jaylib;


public class GUITest extends BerrayApplication implements CoreComponentShortcuts {
  @Override
  public void game() {
    debug = true;


    GameObject button = add(
        rect(100, 100),
        pos(100, 100),
        area(),
        anchor(AnchorType.TOP_LEFT)
    );



    button.on("click", event -> System.out.println("click two"));

  }

  @Override
  public void initWindow() {
    width(1024);
    height(768);
    background(Color.GRAY);
    title("GUI Test");
  }

  public static void main(String[] args) {
    new GUITest().runGame();
  }
}
