package com.berray.tests;

import com.berray.BerrayApplication;
import com.berray.GameObject;
import com.berray.components.AnchorType;
import com.berray.math.Vec2;
import com.raylib.Jaylib;

import static com.berray.AssetManager.loadMusic;
import static com.berray.AssetManager.loadSprite;
import static com.berray.components.RotateComponent.rotate;
import static com.berray.components.SpriteComponent.sprite;
import static com.raylib.Jaylib.RED;

public class ShapeTest  extends BerrayApplication {

  @Override
  public void initWindow() {
    width(1024);
    height(768);
    background(RED);
    title("Pong Game");
  }

  @Override
  public void game() {

    loadSprite("berry", "resources/berry.png");
    loadMusic("wind", "resources/wind.mp3");

    debug = true;

    GameObject infoTxt = add(
        text("Hello im a berry")
    );

    GameObject startTxt = add(
        text("Press Space to start...not"),
        pos(1024 / 2, 768 / 2),
        area()
    );

    GameObject rect = add(
        rect(20, 80),
        pos(129, 83),
        area()
    );

    GameObject rect2 = add(
        rect(20, 80),
        pos(300, 300),
        rotate(45),
        "foo",
        area()
    );

    GameObject rect3 = add(
        rect(80, 80),
        pos(40, 768 - 40)
    );

    GameObject circle = add(
        circle(80),
        pos(400, 183),
        area()
    );

    GameObject berry = add(
        sprite("berry"),
        pos(110, 100),
        anchor(AnchorType.TOP_LEFT),
        rotate(45),
        area()
    );

    GameObject berry2 = add(
        sprite("berry"),
        pos(500, 100),
        area()
    );

    on("mousePress", (event) -> {
      Vec2 pos = event.getParameter(0);
      berry.set("pos", pos);
    });

    game.onUpdate("sprite", event -> {
      GameObject gameObject = event.getParameter(0);
      Vec2 pos = gameObject.get("pos");
      int mouseY = Jaylib.GetMouseY();
      if (pos != null) {
        // Note: this updates the pos inside the component
        pos.setY(mouseY);
        gameObject.set("pos", pos);
      }
    });
  }


  public static void main(String[] args) {
    new ShapeTest().runGame();
  }

}
