package com.berray.tests;

import com.berray.BerrayApplication;
import com.berray.GameObject;
import com.berray.assets.SpriteAtlas;
import com.berray.components.CoreComponentShortcuts;
import com.berray.components.core.AnchorType;
import com.berray.components.core.Component;
import com.berray.event.Event;
import com.berray.math.Collision;
import com.berray.math.Rect;
import com.berray.math.Vec2;
import com.berray.tests.level.LevelBuilder;
import com.berray.tests.level.LevelGameObject;
import com.raylib.Jaylib;
import com.raylib.Raylib;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static com.berray.assets.Animation.anim;
import static com.berray.assets.AssetManager.loadSpriteAtlas;
import static com.berray.assets.SpriteSheet.spriteSheet;
import static com.berray.components.core.DebugComponent.debug;
import static com.raylib.Raylib.*;

public class SpriteAtlasTest extends BerrayApplication implements CoreComponentShortcuts {

  @Override
  public void game() {
    loadSpriteAtlas("dungeonAtlas", "resources/dungeon.png", SpriteAtlas.atlas()
        .sheet("hero", spriteSheet()
            .x(128)
            .y(196)
            .width(144)
            .height(28)
            .sliceX(9)
            .anim("idle", anim()
                .from(0)
                .to(3)
                .speed(3)
                .loop(true))
            .anim("run", anim()
                .from(4)
                .to(7)
                .speed(10)
                .loop(true))
            .anim("hit", 8))
        .sheet("ogre", spriteSheet()
            .x(16)
            .y(320)
            .width(256)
            .height(32)
            .sliceX(8)
            .anim("idle", anim()
                .from(0)
                .to(3)
                .speed(3)
                .loop(true))
            .anim("run", anim()
                .from(4)
                .to(7)
                .speed(10)
                .loop(true)))
        .sheet("floor", spriteSheet()
            .x(16)
            .y(64)
            .width(48)
            .height(48)
            .sliceX(3)
            .sliceY(3)
        )
        .sheet("chest", spriteSheet()
            .x(304)
            .y(304)
            .width(48)
            .height(16)
            .sliceX(3)
            .anim("open", anim()
                .from(0)
                .to(2)
                .speed(20)
                .loop(false))
            .anim("close", anim()
                .from(2)
                .to(0)
                .speed(20)
                .loop(false)))
        .sheet("sword", spriteSheet()
            .x(322)
            .y(81)
            .width(12)
            .height(30))
        .sheet("wall", spriteSheet()
            .x(16)
            .y(16)
            .width(16)
            .height(16))
        .sheet("wall_top", spriteSheet()
            .x(16)
            .y(0)
            .width(16)
            .height(16))
        .sheet("wall_left", spriteSheet()
            .x(16)
            .y(128)
            .width(16)
            .height(16))
        .sheet("wall_right", spriteSheet()
            .x(0)
            .y(128)
            .width(16)
            .height(16))
        .sheet("wall_topleft", spriteSheet()
            .x(32)
            .y(128)
            .width(16)
            .height(16))
        .sheet("wall_topright", spriteSheet()
            .x(48)
            .y(128)
            .width(16)
            .height(16))
        .sheet("wall_botleft", spriteSheet()
            .x(32)
            .y(144)
            .width(16)
            .height(16))
        .sheet("wall_botright", spriteSheet()
            .x(48)
            .y(144)
            .width(16)
            .height(16))
    );

    GameObject level = add(
        pos(Vec2.origin()),
        scale(3)
    );

    layers("default", "actor", "weapon");

    LevelGameObject floor = new LevelBuilder()
        .tileWidth(16)
        .tileHeight(16)
        .tile(' ', tile -> tile.components(
            sprite("floor").frame(rand(0, 8)),
            anchor(AnchorType.TOP_LEFT)
        ))
        .level(new String[]{
            "xxxxxxxxxx",
            "          ",
            "          ",
            "          ",
            "          ",
            "          ",
            "          ",
            "          ",
            "          ",
            "          "});
    level.add(floor);

// objects
    LevelGameObject objects = new LevelBuilder()
        .tileWidth(16)
        .tileHeight(16)
        .tile('$', tile -> tile.components(
            sprite("chest"),
            anchor(AnchorType.TOP_LEFT),
            area(),
            body(true),
            tile().obstacle(true),
            "chest",
            property("opened", false)
        ))
        .tile('a', tile -> tile.components(
            sprite("wall_botleft"),
            anchor(AnchorType.TOP_LEFT),
            area(new Rect(0, 0, 4, 16)),
            body(true),
            tile().obstacle(true)
        ))
        .tile('b', tile -> tile.components(
            sprite("wall_botright"),
            anchor(AnchorType.TOP_LEFT),
            area(new Rect(12, 0, 4, 16)),
            body(true),
            tile().obstacle(true)
        ))
        .tile('c', tile -> tile.components(
            sprite("wall_topleft"),
            anchor(AnchorType.TOP_LEFT),
            area(),
            body(true),
            tile().obstacle(true)
        ))
        .tile('d', tile -> tile.components(
            sprite("wall_topright"),
            anchor(AnchorType.TOP_LEFT),
            area(),
            body(true),
            tile().obstacle(true)
        ))
        .tile('w', tile -> tile.components(
            sprite("wall"),
            anchor(AnchorType.TOP_LEFT),
            area(),
            body(true),
            tile().obstacle(true)
        ))
        .tile('t', tile -> tile.components(
            sprite("wall_top"),
            anchor(AnchorType.TOP_LEFT),
            area(new Rect(0, 12, 16, 4)),
            body(true),
            tile().obstacle(true)
        ))
        .tile('l', tile -> tile.components(
            sprite("wall_left"),
            anchor(AnchorType.TOP_LEFT),
            area(new Rect(0, 0, 4, 16)),
            body(true),
            tile().obstacle(true)
        ))
        .tile('r', tile -> tile.components(
            sprite("wall_right"),
            anchor(AnchorType.TOP_LEFT),
            area(new Rect(12, 0, 4, 16)),
            body(true),
            tile().obstacle(true)
        ))
        .level(new String[]{
            "tttttttttt",
            "cwwwwwwwwd",
            "l        r",
            "l        r",
            "l        r",
            "l      $ r",
            "l        r",
            "l $      r",
            "attttttttb",
            "wwwwwwwwww"});
    level.add(objects);

    GameObject player = level.add(
        sprite("hero").anim("idle"),
        area(new Rect(0, 6, 12, 12)),
        body(),
        anchor(AnchorType.CENTER),
        tile(),
        layer("actor"),
        pos(2 * 16, 2 * 16)
    );

    GameObject sword = player.add(
        pos(-4, 9),
        sprite("sword"),
        anchor(AnchorType.BOTTOM),
        rotate(0),
        spin(),
        layer("weapon")
    );

    level.add(
        sprite("ogre"),
        anchor(AnchorType.BOTTOM),
        area().scale(0.5f),
        body(true),
        tile().obstacle(true),
        pos(5 * 16, 4 * 16),
        layer("actor")
    );


    onKeyPress(Raylib.KEY_SPACE, event -> {
      boolean interacted = false;

      for (Collision col : player.<Collection<Collision>>getOrDefault("collisions", Collections.emptyList())) {
        GameObject c = col.getOther();
        if (c.is("chest")) {
          if (c.getProperty("opened")) {
            c.doAction("play", "close");
            c.setProperty("opened", false);
          } else {
            c.doAction("play", "open");
            c.setProperty("opened", true);
          }
          interacted = true;
        }
      }

      if (!interacted) {
        sword.doAction("spin");
      }
    });

    int SPEED = 120;

    onKeyDown(KEY_RIGHT, (event) -> {
      player.doAction("move", new Vec2(SPEED, 0), frameTime());
      player.set("flipX", false);
      sword.set("flipX", false);
      sword.set("pos", new Vec2(-4, 9));
    });

    onKeyDown(KEY_LEFT, (event) -> {
      player.doAction("move", new Vec2(-SPEED, 0), frameTime());
      player.set("flipX", true);
      sword.set("flipX", true);
      sword.set("pos", new Vec2(4, 9));
    });

    onKeyDown(KEY_UP, (event) -> {
      player.doAction("move", new Vec2(0, -SPEED), frameTime());
    });

    onKeyDown(KEY_DOWN, (event) -> {
      player.doAction("move", new Vec2(0, SPEED), frameTime());
    });

    Arrays.asList(KEY_LEFT, KEY_RIGHT, KEY_UP, KEY_DOWN).forEach((key) -> {
      onKeyPress(key, (event) -> {
        player.doAction("play", "run");
      });
      onKeyRelease(key, (event) -> {
        if (
            !IsKeyDown(KEY_LEFT)
                && !IsKeyDown(KEY_RIGHT)
                && !IsKeyDown(KEY_UP)
                && !IsKeyDown(KEY_DOWN)
        ) {
          player.doAction("play", "idle");
        }
      });
    });
  }


  @Override
  public void initWindow() {
    width(500);
    height(500);
    background(Jaylib.GRAY);
    title("Sprite Atlas Test");
  }

  public static void main(String[] args) {
    new SpriteAtlasTest().runGame();
  }


  private SpinComponent spin() {
    return new SpinComponent();
  }

  /**
   * custom component for this test.
   */
  private static class SpinComponent extends Component {
    private boolean spinning = false;

    public SpinComponent() {
      super("spin", "rotate");
    }

    @Override
    public void add(GameObject gameObject) {
      super.add(gameObject);
      registerAction("spin", (Consumer<List<Object>>) (params) -> spinning = true);
      on("update", this::update);
    }

    public void update(Event event) {
      float deltaTime = event.getParameter(0);
      if (spinning) {
        float angle = gameObject.get("angle");
        angle += 1200 * deltaTime;
        if (angle >= 360) {
          angle = 0;
          spinning = false;
        }
        gameObject.set("angle", angle);
      }
    }
  }
}
