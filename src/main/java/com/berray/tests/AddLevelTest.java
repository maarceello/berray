package com.berray.tests;

import com.berray.BerrayApplication;
import com.berray.GameObject;
import com.berray.assets.CoreAssetShortcuts;
import com.berray.components.CoreComponentShortcuts;
import com.berray.components.core.AnchorType;
import com.berray.event.PhysicsCollideEvent;
import com.berray.math.Color;
import com.berray.math.Vec2;
import com.berray.tests.level.LevelBuilder;
import com.berray.tests.level.LevelGameObject;

import static com.berray.event.CoreEvents.UPDATE;
import static com.berray.objects.addon.ObjectDebug.objectDebug;
import static com.berray.objects.core.Label.label;
import static com.raylib.Raylib.*;
import static java.lang.Boolean.FALSE;


public class AddLevelTest extends BerrayApplication implements CoreComponentShortcuts, CoreAssetShortcuts {
  private static final int SPEED = 480;

  private int score = 0;

  @Override
  public void game() {

    debug = true;

    game.setGravity(2400);
    targetFps = -1;

    loadSprite("bean", "resources/berry.png");
    loadSprite("coin", "resources/coin.png");
    loadSprite("spike", "resources/spike.png");
    loadSprite("grass", "resources/grass.png");

    LevelBuilder levelBuilder = new LevelBuilder();
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
        new String[]{
            "@      ",
            "   ^ $$",
            "======="
        },
        pos(100, 200),
        anchor(AnchorType.TOP_LEFT)
    );

    add(level);

    // Get the player object from tag
    GameObject player = level.getTagStream("player").findFirst().orElse(null);

    // Add Debug Infos for Player object
    add(objectDebug(player),
        pos(0, 0),
        anchor(AnchorType.TOP_LEFT));
    // add score label
    add(label(() -> "Score: " + score),
        pos(width() / 2.0f, 0),
        anchor(AnchorType.TOP));

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
    player.on(UPDATE, (event) -> {
      Vec2 pos = player.get("pos");
      if (pos.getY() > width()) {
        player.set("pos", level.tile2Pos(0, 0));
      }
    });

    // Eat the coin!
    player.onCollide("coin", (PhysicsCollideEvent event) -> {
      GameObject coin = event.getCollisionPartner();
      destroy(coin);
      play("score");
      score++;
    });
  }

  @Override
  public void initWindow() {
    width(1024);
    height(768);
    background(Color.GRAY);
    title("Add Level Test");
  }

  public static void main(String[] args) {
    new AddLevelTest().runGame();
  }

}
