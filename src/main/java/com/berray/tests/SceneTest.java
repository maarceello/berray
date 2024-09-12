package com.berray.tests;

import com.berray.BerrayApplication;
import com.berray.GameObject;
import com.berray.assets.CoreAssetShortcuts;
import com.berray.components.CoreComponentShortcuts;
import com.berray.components.core.AnchorType;
import com.berray.math.Color;
import com.berray.math.Vec2;
import com.berray.tests.level.LevelBuilder;
import com.berray.tests.level.LevelGameObject;
import com.raylib.Jaylib;

import java.util.concurrent.atomic.AtomicInteger;

import static com.raylib.Raylib.*;

public class SceneTest extends BerrayApplication implements CoreComponentShortcuts, CoreAssetShortcuts {


  @Override
  public void game() {
    loadSprite("bean", "resources/berry.png");
    loadSprite("coin", "resources/coin.png");
    loadSprite("spike", "resources/spike.png");
    loadSprite("grass", "resources/grass.png");
    loadSprite("ghosty", "resources/ghosty.png");
    loadSprite("portal", "resources/portal.png");

    game.setGravity(2400);

    float SPEED = 480;

    // Design 2 levels
    String[][] LEVELS = new String[][]{
        new String[]{
            "@  ^ $$ >",
            "========="
        },
        new String[]{
            "@   $   >",
            "=   =   =",
        }
    };

    LevelBuilder levelBuilder = new LevelBuilder(64, 64)
        .tile('@', tile -> tile.components(
            sprite("bean"),
            area(),
            body(),
            anchor(AnchorType.BOTTOM),
            "player"))
        .tile('=', tile -> tile.components(
            sprite("grass"),
            area(),
            body(true),
            anchor(AnchorType.BOTTOM)))
        .tile('$', tile -> tile.components(
            sprite("coin"),
            area(),
            anchor(AnchorType.BOTTOM),
            "coin"))
        .tile('^', tile -> tile.components(
            sprite("spike"),
            area(),
            anchor(AnchorType.BOTTOM),
            "danger"))
        .tile('>', tile -> tile.components(
            sprite("portal"),
            area(),
            anchor(AnchorType.BOTTOM),
            "portal"));

    scene("game", sceneDef -> {
      final int levelIdx = sceneDef.getParameter(0);
      AtomicInteger score = new AtomicInteger(sceneDef.getParameter(1));

      LevelGameObject level = levelBuilder.level(LEVELS[levelIdx]);
      sceneDef.add(
          level,
          pos(100,200)
          );

      GameObject player = level.getTagStream("player").findFirst().orElseThrow(() -> new IllegalStateException("Player object not found"));

      // Score counter text
      GameObject scoreLabel = sceneDef.add(
          text("score: "+ score.get()),
          pos(12, 0),
          anchor(AnchorType.TOP_LEFT)
      );


      onKeyPress(KEY_SPACE, event -> {
        if (Boolean.TRUE.equals(player.get("grounded", false))) {
          player.doAction("jump");
        }
      });

      onKeyDown(KEY_LEFT, event -> player.doAction("move", new Vec2(-SPEED, 0), frameTime()));

      onKeyDown(KEY_RIGHT, event -> player.doAction("move", new Vec2(SPEED, 0), frameTime()));

      player.onCollide("danger", event -> {
        player.set("pos", Vec2.origin());
        // Go to "lose" scene when we hit a "danger"
        go("lose");
      });

      player.onCollide("coin", event -> {
        GameObject coin = event.getParameter(0);
        destroy(coin);
        play("score");
        score.getAndIncrement();
        scoreLabel.set("text", String.valueOf(score.get()));
      });

      // Fall death
      player.on("update", event -> {
        if (player.get("pos", Vec2.origin()).getY() >= 480) {
          go("lose");
        }
      });

      // Enter the next level on portal
      player.onCollide("portal", event -> {
        play("portal");
        if (levelIdx < LEVELS.length - 1) {
          // If there's a next level, go() to the same scene but load the next level
          go("game", levelIdx + 1, score.get());
        } else {
          // Otherwise we have reached the end of game, go to "win" scene!
          go("win", score.get());
        }
      });
    });

    scene("lose", sceneDef -> {
      add(
          text("You Lose"),
          pos(12, 0),
          anchor(AnchorType.TOP_LEFT)
      );

      // Press any key to go back
      onKeyPress(event -> start());
    });

    scene("win", sceneDef -> {
      int score = sceneDef.getParameter(0);
      sceneDef.add(
          text("You grabbed " + score + " coins!!!"),
          anchor(AnchorType.TOP),
          pos(width() / 2.0f, 12)
      );

      sceneDef.onKeyPress(event -> start());
    });

    start();
  }

  public void start() {
    // Start with the "game" scene, with initial parameters
    go("game", 0, 0);
  }

  @Override
  public void initWindow() {
    width(1024);
    height(768);
    background(Color.GRAY);
    title("Scenes Test");
  }

  public static void main(String[] args) {
    new SceneTest().runGame();
  }

}
