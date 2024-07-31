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
  /**
   * Gravity vector.
   */
  private Vec2 gravity;
  /**
   * normalized gravity vector.
   */
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
  public void update(float frameTime) {
    root.update(frameTime);
  }

  /**
   * Draw all game objects
   */
  public void draw() {
    root.draw();
  }

  private Vec2 collides(Rect a, Rect b) {
    float rax1 = a.getX();
    float ray1 = a.getY();
    float rax2 = a.getX() + a.getWidth();
    float ray2 = a.getY() + a.getHeight();

    float rbx1 = b.getX();
    float rby1 = b.getY();
    float rbx2 = b.getX() + b.getWidth();
    float rby2 = b.getY() + b.getHeight();

    if (
        rax1 >= rbx2 || // a is to the right of b
            ray1 >= rby2 || // a is below b
            rax2 < rbx1 || // a is to the left of b
            ray2 < rby1 // a is above b
    ) {
      // not colliding
      return null;
    }

    // find the shortest vector in which to move the object a so the collision is resolved
    Vec2 displacement = Vec2.origin();
    float minDistance = Float.MAX_VALUE;
    // upper edge in collision? Move upper edge down
    if (ray1 > rby1 && ray1 < rby2) {
      float distance = rby2 - ray1;
      if (distance < minDistance) {
        minDistance = distance;
        displacement = new Vec2(0, distance);
      }
    }
    // lower edge in collision? Move lower edge up
    if (ray2 >= rby1 && ray2 < rby2) {
      float distance = ray2 - rby1;
      if (distance < minDistance) {
        minDistance = distance;
        displacement = new Vec2(0, -distance);
      }
    }
    // left edge in collision? Move left edge to the right
    if (rax1 > rbx1 && rax1 < rbx2) {
      float distance = rbx2 - rax1;
      if (distance < minDistance) {
        minDistance = distance;
        displacement = new Vec2(distance, 0);
      }
    }
    // right edge in collision? Move right edge to the left
    if (rax2 >= rbx1 && rax2 < rbx2) {
      float distance = rax2 - rbx1;
      if (distance < minDistance) {
        minDistance = distance;
        displacement = new Vec2(-distance,0 );
      }
    }

    return displacement;
  }

  public void updateCollisions() {
    // optimization and collision detection between arbitrarily oriented polygons
    // Reduce number of object to object collision detections
    // * some kind of bsp or quad tree
    // * https://en.wikipedia.org/wiki/Sweep_and_prune
    // Separating Axis Theorem:
    // * https://www.sevenson.com.au/programming/sat/
    // * https://code.tutsplus.com/collision-detection-using-the-separating-axis-theorem--gamedev-169t

    // linearize the object tree
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

    System.out.println("checking "+areaObjects.size()+" objects");
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

    Set<String> thisCollisionIgnores = obj.getOrDefault("collisionIgnore", Collections.emptySet());

    for (GameObject other : others) {
      // TODO: if (checked.has(other.id)) continue;
      if (other.getBoundingBox() == null) {
        continue;
      }
      // should we ignore the other object based on tags?
      Optional<String> ignoreTag = thisCollisionIgnores.stream()
          .filter(other::is)
          .findFirst();
      if (ignoreTag.isPresent()) {
        continue;
      }

      Vec2 res = collides(area, other.getBoundingBox());
      if (res != null) {
        System.out.println("collision "+obj.getId()+" with "+other.getId()+" with displacement "+res);
        Collision col1 = new Collision(obj, other, res);
        obj.trigger("collideUpdate", other, col1);
        Collision col2 = col1.reverse();
        // resolution only has to happen only once
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

  /**
   * removes the game object and all of its children from the game.
   */
  public void destroy(GameObject gameObject) {

  }
}