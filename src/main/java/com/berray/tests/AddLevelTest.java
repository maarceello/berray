package com.berray.tests;

import com.berray.BerrayApplication;
import com.berray.GameObject;
import com.berray.components.CoreComponentShortcuts;
import com.berray.components.core.AnchorType;
import com.berray.math.Vec2;
import com.berray.objects.addon.ObjectDebug;
import com.berray.tests.level.LevelBuilder;
import com.berray.tests.level.LevelGameObject;
import com.raylib.Jaylib;

import static com.berray.AssetManager.loadSprite;
import static com.raylib.Raylib.*;
import static java.lang.Boolean.FALSE;


public class AddLevelTest extends BerrayApplication implements CoreComponentShortcuts {
  private static final int SPEED = 480;
  private LevelBuilder levelBuilder;

  @Override
  public void game() {

    debug = true;

    game.setGravity(2400);
    targetFps = 60;

    loadSprite("bean", "resources/berry.png");
    loadSprite("coin", "resources/coin.png");
    loadSprite("spike", "resources/spike.png");
    loadSprite("grass", "resources/grass.png");

    this.levelBuilder = new LevelBuilder();
    levelBuilder
        .tileWidth(64)
        .tileHeight(64)
        .tile('@', tile -> tile.components(
            sprite("bean"),
            area(),
            body(),
            anchor(AnchorType.BOTTOM),
            "player"
        ))
        .tile('=', tile -> tile.components(
            sprite("grass"),
            area().ignoreCollisionWith("terrain"),
            body(true),
            anchor(AnchorType.BOTTOM),
            "terrain"
        ))
        .tile('$', tile -> tile.components(
            sprite("coin"),
            area().ignoreCollisionWith("terrain"),
            anchor(AnchorType.BOTTOM),
            "coin"
        ))
        .tile('^', tile -> tile.components(
            sprite("spike"),
            area().ignoreCollisionWith("terrain"),
            anchor(AnchorType.BOTTOM),
            "danger",
            "terrain"
        ));

    LevelGameObject level = levelBuilder.level(
        "@      ",
        "   ^ $$",
        "=======");
    level.set("pos", new Vec2(100, 200));
    level.set("anchor", AnchorType.TOP_LEFT);

    add(level);

    // Get the player object from tag
    GameObject player = level.getTagStream("player").findFirst().orElse(null);

    add(new ObjectDebug(player),
        pos(0,0),
        anchor(AnchorType.TOP_LEFT));

    // Movements
    onKeyPress(KEY_SPACE, (event) -> {
      if (player.<Boolean>getOrDefault("grounded", FALSE)) {
        player.doAction("jump");
      }
    });

    onKeyDown(KEY_LEFT, (event) -> {
      player.doAction("move", new Vec2(-SPEED, 0), frameTime());
    });

    onKeyDown(KEY_RIGHT, (event) -> {
      player.doAction("move", new Vec2(SPEED, 0), frameTime());
    });

    // Back to the original position if hit a "danger" item
    player.onCollide("danger", (event) -> {
      player.set("pos", level.tile2Pos(0, 0));
    });

    // back to start when the player falls off screen
    player.on("update", (event) -> {
      Vec2 pos = player.get("pos");
      if (pos.getY() > width()) {
        player.set("pos", level.tile2Pos(0, 0));
      }
    });

    // Eat the coin!
    player.onCollide("coin", (event) -> {
      GameObject coin = event.getParameter(0);
      destroy(coin);
      play("score");
    });
  }

  @Override
  public float frameTime() {
    return 1.0f/60;
  }

  @Override
  public void initWindow() {
    width(1024);
    height(768);
    background(Jaylib.GRAY);
    title("Add Level Test");
  }

  public static void main(String[] args) {
    new AddLevelTest().runGame();
  }

}
