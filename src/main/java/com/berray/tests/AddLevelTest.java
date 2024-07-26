package com.berray.tests;

import com.berray.BerrayApplication;
import com.berray.GameObject;
import com.berray.components.core.AnchorType;
import com.berray.math.Vec2;
import com.raylib.Jaylib;

import static com.berray.AssetManager.loadSprite;
import static com.berray.components.core.PosComponent.pos;
import static com.berray.components.core.SpriteComponent.sprite;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;


public class AddLevelTest extends BerrayApplication {


  // this needs to be a separate level class, with scenes and stuff
  // but there i had problems to call "add()" since im not in the berray context anymore
  // have fun mato

  class Level {
    public void addLevel(float tileWidth, float tileHeight, String[] levelString, Vec2 pos, Map<Character, Supplier<GameObject>> tileMappings) {
      for (int y = 0; y < levelString.length; y++) {
        for (int x = 0; x < levelString[y].length(); x++) {
          char tile = levelString[y].charAt(x);
          if (tileMappings.containsKey(tile)) {
            GameObject gameObject = tileMappings.get(tile).get();
            gameObject.addComponents(pos(pos.getX() + (x * tileWidth), pos.getY() + (y * tileHeight)));
          }

        }
      }
    }
  }


  @Override
  public void game() {

    // Load Resources
    loadSprite("grass", "resources/grass.png");
    loadSprite("key", "resources/key.png");
    loadSprite("berry", "resources/berry.png");
    loadSprite("door", "resources/door.png");

    int tileWidth = 64;
    int tileHeight = 64;
    String[] level = {
        "===|====",
        "=      =",
        "= $    =",
        "=    a =",
        "=    @ =",
        "=      =",
        "========",
    };
    Vec2 startingPos = new Vec2(100.0f, 100.0f);
    Map<Character, Supplier<GameObject>> tiles = new HashMap<>();
    tiles.put('=', () -> add(
        sprite("grass"),
        anchor(AnchorType.CENTER)
    ));
    tiles.put('$', () -> add(
        sprite("key"),
        anchor(AnchorType.CENTER)
    ));
    tiles.put('@', () -> add(
        sprite("berry"),
        anchor(AnchorType.CENTER)
    ));
    tiles.put('|', () -> add(
        sprite("door"),
        anchor(AnchorType.CENTER)
    ));

    // stupid
    addLevelTemp(tileWidth, tileHeight, level, startingPos, tiles);

  }
  // more stupid
  private void addLevelTemp(int tileWidth, int tileHeight, String[] levelString, Vec2 pos, Map<Character, Supplier<GameObject>> tileMappings) {
    Level level = new Level();
    level.addLevel(tileWidth, tileHeight, levelString, pos, tileMappings);
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
