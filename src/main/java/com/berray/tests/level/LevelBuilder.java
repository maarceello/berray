package com.berray.tests.level;

import com.berray.math.Vec2;

import java.util.*;
import java.util.function.Consumer;

import static com.berray.components.core.PosComponent2d.pos;

/**
 * Class responsible for creating level Game Objects.
 */
public class LevelBuilder {
  private int tileWidth;
  private int tileHeight;
  private Map<Character, Consumer<TileBuilder>> charToTile = new HashMap<>();

  public LevelBuilder(int tileWidth, int tileHeight) {
    this.tileWidth = tileWidth;
    this.tileHeight = tileHeight;
  }

  public LevelBuilder() {
    this(16, 16);
  }

  public LevelBuilder tileWidth(int tileWidth) {
    this.tileWidth = tileWidth;
    return this;
  }

  public LevelBuilder tileHeight(int tileHeight) {
    this.tileHeight = tileHeight;
    return this;
  }

  public LevelBuilder tile(char tileChar, Consumer<TileBuilder> tileBuilder) {
    charToTile.put(tileChar, tileBuilder);
    return this;
  }

  public LevelGameObject level(String[] levelStrings, Object ... levelComponents) {
    LevelGameObject level = new LevelGameObject(tileWidth, tileHeight);
    level.addComponents(
        levelComponents
    );
    for (int y = 0; y < levelStrings.length; y++) {
      for (int x = 0; x < levelStrings[y].length(); x++) {
        char tile = levelStrings[y].charAt(x);
        if (charToTile.containsKey(tile)) {
          int finalX = x;
          int finalY = y;
          charToTile.get(tile).accept(components -> {
            List<Object> componentsList = new ArrayList<>(Arrays.asList(components));
            componentsList.add(pos(new Vec2(finalX * tileWidth, finalY * tileHeight)));
            level.add().addComponents(componentsList);
          });
        }
      }
    }
    return level;
  }
}
