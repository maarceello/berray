package com.berray.tests;

import com.berray.GameObject;
import com.berray.components.core.AnchorType;

import java.util.HashMap;
import java.util.Map;

import static com.berray.components.core.AnchorComponent.anchor;
import static com.berray.components.core.PosComponent.pos;
import static com.berray.components.core.SpriteComponent.sprite;

/** Class responsible for creating level Game Objects. */
public class LevelBuilder {
  private final int tileWidth;
  private final int tileHeight;
  private Map<Character, String> tileToSprite =  new HashMap<>();

  public LevelBuilder() {
    tileToSprite.put('=', "grass");
    tileToSprite.put('$', "key");
    tileToSprite.put('@', "berry");
    tileToSprite.put('|', "door");
    this.tileWidth = 64;
    this.tileHeight = 64;
  }

  // Variante 1: addLevel bekommt den Hauptknoten übergeben und fügt die Tiles dort hinzu
  public void addLevel1(GameObject rootNode, String[] levelString) {
    for (int y = 0; y < levelString.length; y++) {
      for (int x = 0; x < levelString[y].length(); x++) {
        char tile = levelString[y].charAt(x);
        if (tileToSprite.containsKey(tile)) {
          rootNode.add(
              sprite(tileToSprite.get(tile)),
              pos(x * tileWidth, y * tileHeight),
              anchor(AnchorType.TOP_LEFT)
          );
        }
      }
    }
  }

  // Variante 2: das GameObject wird vom LevelBuilder erstellt. Der Rest ist gleich
  // Note: das rootObject hat keinerlei Komponenten, also auch keine Position. Es ist ausschließlich
  // ein Kontainer für die Tiles. Alle benötigten Komponenten müssen im game() hinzugefügt werden.
  public GameObject level(String[] levelString) {
    GameObject rootNode = new GameObject();
    for (int y = 0; y < levelString.length; y++) {
      for (int x = 0; x < levelString[y].length(); x++) {
        char tile = levelString[y].charAt(x);
        if (tileToSprite.containsKey(tile)) {
          rootNode.add(
              sprite(tileToSprite.get(tile)),
              pos(x * tileWidth, y * tileHeight),
              anchor(AnchorType.TOP_LEFT)
          );
        }
      }
    }
    return rootNode;
  }
}
