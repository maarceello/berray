package com.berray.tests;

import com.berray.BerrayApplication;
import com.berray.GameObject;
import com.berray.assets.CoreAssetShortcuts;
import com.berray.components.CoreComponentShortcuts;
import com.berray.components.core.AnchorType;
import com.berray.math.Color;
import com.berray.math.Vec2;
import com.berray.objects.core.Label;
import com.raylib.Jaylib;
import com.raylib.Raylib;

import static com.berray.assets.Animation.anim;
import static com.berray.assets.SpriteSheet.spriteSheet;

public class SpriteTest extends BerrayApplication implements CoreComponentShortcuts, CoreAssetShortcuts {
  @Override
  public void game() {
    float SPEED = 120;
    float JUMP_FORCE = 240;

    game.setGravity(640);


    loadSpriteSheet("dino", "resources/dino_big.png",
        spriteSheet()
            .sliceX(9)
            .anim("idle", anim()
                .from(0)
                .to(3)
                .speed(5) // Frame per second
                .loop(true))
            .anim("run", anim()
                .from(4)
                .to(7)
                .speed(10) // Frame per second
                .loop(true))
            .anim("jump", 8) // This animation only has 1 frame
    );

    GameObject player = add(
        sprite("dino"),
        pos(center()),
        anchor(AnchorType.CENTER),
        area(),
        body()
    );

    player.doAction("play", "idle");

    // add plattform
    add(
        rect(width(), 24),
        area(),
        anchor(AnchorType.TOP_LEFT),
        pos(0, height() - 24),
        body(true)
    );



// Switch to "idle" or "run" animation when player hits ground
    player.on("ground", (event) -> {
      if (!Raylib.IsKeyDown(Raylib.KEY_LEFT) && !Raylib.IsKeyDown(Raylib.KEY_RIGHT)) {
        player.doAction("play", "idle");
      } else {
        player.doAction("play", "run");
      }
    });

    player.on("animEnd", event -> {
      String anim = event.getParameter(0);
      if (anim.equals("idle")) {
        // You can also register an event that runs when certain anim ends
      }
    });

    onKeyPress(Jaylib.KEY_SPACE, (event) -> {
      if (player.getOrDefault("grounded", false)) {
        player.doAction("jump", JUMP_FORCE);
        player.doAction("play", "jump");
      }
    });

    onKeyDown(Raylib.KEY_LEFT, (event) -> {
      player.doAction("move", new Vec2(-SPEED, 0), frameTime());
      player.set("flipX", true);
      // .play() will reset to the first frame of the anim, so we want to make sure it only runs when the current animation is not "run"
      if (player.getOrDefault("grounded", false) && !"run".equals(player.get("curAnim")  )) {
        player.doAction("play","run");
      }
    });

    onKeyDown(Raylib.KEY_RIGHT, (event) -> {
      player.doAction("move", new Vec2(SPEED, 0), frameTime());
      player.set("flipX", false);
      if (player.getOrDefault("grounded", false) && !"run".equals(player.get("curAnim")  )) {
        player.doAction("play","run");
      }
    });

    on("keyUp", (event) -> {
      int key = event.getParameter(0);
      if (key == Raylib.KEY_LEFT || key == Raylib.KEY_RIGHT) {
        // Only reset to "idle" if player is not holding any of these keys
        if (player.getOrDefault("grounded", false)) {
          player.doAction("play", "idle");
        }
      }
    });

    add(
        Label.label(() -> "Anim: "+player.get("curAnim")+"\nFrame: "+player.get("frame")),
        pos(Vec2.origin()),
        anchor(AnchorType.TOP_LEFT),
        color(0,0,0)
    );

  }

  @Override
  public void initWindow() {
    width(500);
    height(500);
    background(Color.GRAY);
    title("Sprite Sheet Test");
  }

  public static void main(String[] args) {
    new SpriteTest().runGame();
  }
}
