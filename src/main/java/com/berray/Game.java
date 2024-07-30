package com.berray;

import com.berray.event.EventListener;
import com.berray.event.EventManager;
import com.berray.math.Collision;
import com.berray.math.Rect;
import com.berray.math.Vec2;
import com.raylib.Jaylib;

import java.util.*;
import java.util.stream.Collectors;

public class Game {
  /** Gravity vector. */
  private Vec2 gravity;
  /** normalized gravity vector. */
  private Vec2 gravityDirection;
  private final GameObject root;

  private EventManager eventManager = new EventManager();

  // Constructor
  public Game() {
    root = new GameObject(this);
    init();
  }

  public void init() {
    // forward game objects adds to game event manager
    root.on("add", event -> eventManager.trigger(event.getName(), event.getParameters()));
    // forward update events down the object tree
    on("update", event -> root.trigger(event.getName(), event.getParameters()));

    root.registerGetter("gravityDirection", this::getGravityDirection);
    root.registerGetter("gravity", this::getGravity);
  }

  public float width() {
    return Jaylib.GetRenderWidth();
  }

  public float height() {
    return Jaylib.GetRenderHeight();
  }

  public Vec2 center() {
    return new Vec2(width() / 2, height() / 2);
  }

  public void setGravity(int gravity) {
    this.gravity = new Vec2(0, gravity);
    this.gravityDirection = this.gravity.normalize();
  }

  public Vec2 getGravityDirection() {
    return gravityDirection;
  }

  public Vec2 getGravity() {
    return gravity;
  }

  /**
   * Add a game object to the game
   */
  public GameObject add(Object... components) {
    GameObject newGameObject = root.add(components);
    // forward add events from the new game object to game event manager
    newGameObject.on("add", event -> eventManager.trigger(event.getName(), event.getParameters()));
    return newGameObject;
  }

  /**
   * Add a game object to the game
   */
  public void addChild(GameObject child) {
    root.addChild(child);
  }


  /**
   * Update all game objects
   */
  public void update() {
    root.update(Jaylib.GetFrameTime());
  }

  // Draw all game objects
  public void draw() {
    root.draw();
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

  public void updateCollisions() {
    // linearize the object tree
    // https://en.wikipedia.org/wiki/Sweep_and_prune
    List<GameObject> gameObjects = new ArrayList<>();
    for (GameObject gameObject : root.getChildren()) {
      addGameObjects(gameObject, gameObjects);
    }
    // only keep game objects with area componentLIME
    List<GameObject> areaObjects = gameObjects.stream()
        // only game objects with area component may participate in collision detection
        .filter(gameObject -> gameObject.is("area"))
        // paused game objects don't participate in collision detection
        .filter(gameObject -> !gameObject.isPaused())
        .collect(Collectors.toList());

    for (int i = 0; i < areaObjects.size() - 1; i++) {
      checkObj(areaObjects.get(i), areaObjects.subList(i + 1, areaObjects.size()));
    }

  }

  public void addGameObjects(GameObject current, List<GameObject> allGameObjects) {
    allGameObjects.add(current);
    for (GameObject gameObject : current.getChildren()) {
      addGameObjects(gameObject, allGameObjects);
    }
  }

  public void checkObj(GameObject obj, List<GameObject> others) {
    Rect area = obj.getBoundingBox();

    for (GameObject other : others) {
      // TODO: if (checked.has(other.id)) continue;
      // TODO: check collisionIgnore: should other ignore collisions with objects with specific tags
      if (other.getBoundingBox() == null) {
        continue;
      }

      Vec2 res = collides(area, other.getBoundingBox());
      if (res != null) {
        // TODO: rehash if the object position is changed after resolution?
        Collision col1 = new Collision(obj, other, res);
        obj.trigger("collideUpdate", other, col1);
        Collision col2 = col1.reverse();
        // resolution only has to happen once
        col2.setResolved(col1.isResolved());
        other.trigger("collideUpdate", obj, col2);
      }
    }
  }

  public void on(String event, EventListener listener) {
    eventManager.addEventListener(event, listener);
  }

  public void trigger(String event, Object... params) {
    eventManager.trigger(event, Arrays.asList(params));
  }

  /**
   * Update all game objects
   */
  public void onUpdate(String tag, EventListener eventListener) {
    on("update", event -> {
      GameObject gameObject = event.getParameter(0);
      // only propagate event when the object has the required tag
      if (gameObject.is(tag)) {
        eventListener.onEvent(event);
      }
    });
  }

  /** removes the game object and all of its children from the game. */
  public void destroy(GameObject gameObject) {

  }
}