package com.berray.tests;

import com.berray.BerrayApplication;
import com.berray.GameObject;
import com.berray.components.CoreComponentShortcuts;
import com.berray.components.core.AnchorType;
import com.berray.math.Vec2;
import com.berray.tests.level.LevelBuilder;
import com.berray.tests.level.LevelGameObject;
import com.raylib.Jaylib;

import static com.berray.AssetManager.loadSprite;
import static java.lang.Boolean.FALSE;


public class AddLevelTest extends BerrayApplication implements CoreComponentShortcuts {
  private static final int SPEED = 480;
  private LevelBuilder levelBuilder;

  @Override
  public void game() {

    debug = true;

    game.setGravity(200);


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
            area(),
            body(true),
            anchor(AnchorType.BOTTOM)
        ))
        .tile('$', tile -> tile.components(
            sprite("coin"),
            area(),
            anchor(AnchorType.BOTTOM),
            "coin"
        ))
        .tile('^', tile -> tile.components(
            sprite("spike"),
            area(),
            anchor(AnchorType.BOTTOM),
            "danger"
        ));

    LevelGameObject level = levelBuilder.level(
        "@      ",
        "   ^ $$",
        "=======");
    level.set("pos", new Vec2(100, 200));
    level.set("anchor", AnchorType.TOP_LEFT);

    add(level);

    level.getGameObjectStream().forEach((gameObject) -> {
          System.out.println(gameObject.getId()+": "+gameObject.getTags()+" "+gameObject.getChildren());
        }
    );

    // Get the player object from tag
    GameObject player = level.getTagStream("player").findFirst().orElse(null);

    // Movements
    onKeyPress(Jaylib.KEY_SPACE, (event) -> {
      if (player.<Boolean>getOrDefault("grounded", FALSE)) {
        player.doAction("jump");
      }
    });

    onKeyDown(Jaylib.KEY_LEFT, (event) -> {
      player.doAction("move", -SPEED, 0);
    });

    onKeyDown(Jaylib.KEY_RIGHT, (event) -> {
      player.doAction("move", SPEED, 0);
    });

    // Back to the original position if hit a "danger" item
    player.onCollide("danger", (event) -> {
      player.set("pos", level.tile2Pos(0, 0));
    });

    // Eat the coin!
    player.onCollide("coin", (event) -> {
      GameObject coin = event.getParameter(0);
      destroy(coin);
      play("score");
    });
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
