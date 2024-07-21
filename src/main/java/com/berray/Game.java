package com.berray;

import com.berray.components.AreaComponent;
import com.berray.math.Collision;
import com.berray.math.Rect;
import com.berray.math.Vec2;

import java.util.LinkedList;

public class Game {
  private int gravity;
  private int nextGameObjectId = 0;
  private final LinkedList<GameObject> gameObjects;


  private int width;
  private int height;

  // Constructor
  public Game() {
    this.gameObjects = new LinkedList<>();
    init();
  }

  public void init() {
  }

  // Add a game object to the game
  public GameObject add(Object... components) {
    GameObject gameObject = new GameObject(nextGameObjectId++);
    gameObject.addComponents(components);
    gameObjects.add(gameObject);
    return gameObject;
  }

  // Update all game objects
  public void update() {
    for (GameObject gameObject : gameObjects) {
      gameObject.update();
    }
  }

  // Draw all game objects
  public void draw() {
    for (GameObject gameObject : gameObjects) {
      gameObject.draw();
    }
  }

  private Vec2 collides(Rect a, Rect b) {
    if (
        a.getX() > b.getX() + b.getWidth() || // a is to the right of b
            a.getY() > b.getY() + b.getHeight() || // a is below b
            b.getX() > a.getX() + a.getWidth() || // a is to the left of b
            b.getY() > a.getY() + a.getHeight() // a is above b
    ) {
      // not colliding
      return null;
    }
    // todo: how to calculate displacement?
    return new Vec2(a.getX() - b.getX(), a.getY() - b.getY());
  }

  public void checkFrame() {
    // checkObj(game.root);
    gameObjects.forEach(this::checkObj);
  }

  public void checkObj(GameObject obj) {
    AreaComponent aobj = obj.getComponent(AreaComponent.class);
    if (aobj != null && !obj.isPaused()) {
      // TODO: only update worldArea if transform changed
      Rect area = aobj.worldArea();

      for (GameObject other : gameObjects) {
        if (other.isPaused()) continue;
        // if (!other.exists()) continue;

        // TODO: if (checked.has(other.id)) continue;
        // TODO: check collisionIgnore: should other ignore collisions with objects with specific tags

        Vec2 res = collides(area, other.get("worldArea"));
        if (res != null) {
          // TODO: rehash if the object position is changed after resolution?
          Collision col1 = new Collision(obj, other, res);
          obj.trigger("collideUpdate", other, col1);
          Collision col2 = col1.reverse();
          // resolution only has to happen once
          col2.setResolved(col1.isResolved());
          other.trigger("collideUpdate", aobj, col2);
        }
      }
    }
  }
}