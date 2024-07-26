package com.berray.tests;

import com.berray.BerrayApplication;
import com.berray.GameObject;
import com.raylib.Jaylib;

import static com.berray.AssetManager.loadSprite;


public class AddLevelTest extends BerrayApplication {
  private LevelBuilder levelBuilder;

  @Override
  public void game() {

    // Note: levelBuilder is a field, so we can create the helper method for
    // variant 3
    this.levelBuilder = new LevelBuilder();

    // Load Resources
    loadSprite("grass", "resources/grass.png");
    loadSprite("key", "resources/key.png");
    loadSprite("berry", "resources/berry.png");
    loadSprite("door", "resources/door.png");

    String[] level = {
        "===|====",
        "=      =",
        "= $    =",
        "=    a =",
        "=    @ =",
        "=      =",
        "========",
    };

    // Variante 1: der Hauptknoten wird von der game() kontrolliert bzw. erstellt und
    // übergibt diesen an den LevelBuilder
    GameObject variant1 = add(
        pos(0, 100)
    );
    levelBuilder.addLevel1(variant1, level);
    variant1.add(
        text("Variant 1"),
        pos(150, -30),
        color(255, 203, 0)
    );


    // Variante 2: der LevelBuilder erstellt das GameObject. Da "add()" als ersten Parameter
    // auch ein GameObject akzeptiert, sieht das schon eher "kanonisch"
    GameObject variant2 = add(
        levelBuilder.level(level),
        pos(512, 100)
    );
    variant2.add(
        text("Variant 2"),
        pos(150, -30),
        color(255, 203, 0)
    );

    // Variante 3: wie 2, nur dass 'levelBuilder.level(level)' in eine eigene Methode in dieser
    // Klasse ausgelagert wurde. Da 'levelBuilder' ein Field ist, kann die Methode "level(String[] level)"
    // darauf zugreifen. Jetzt sieht zumindest der Aufruf schön kanonisch aus.
    GameObject variant3 = add(
        level(level),
        pos(1024, 100)
    );
    variant3.add(
        text("Variant 3"),
        pos(150, -30),
        color(255, 203, 0)
    );
  }

  private GameObject level(String[] level) {
    return levelBuilder.level(level);
  }


  @Override
  public void initWindow() {
    width(512*3);
    height(768);
    background(Jaylib.GRAY);
    title("Add Level Test");
  }

  public static void main(String[] args) {
    new AddLevelTest().runGame();
  }

}
