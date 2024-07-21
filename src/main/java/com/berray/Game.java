package com.berray;

import com.berray.components.AreaComponent;
import com.berray.event.EventListener;
import com.berray.event.EventManager;
import com.berray.math.Collision;
import com.berray.math.Rect;
import com.berray.math.Vec2;
import com.raylib.Jaylib;

import java.util.Arrays;

public class Game {
  private int gravity;
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
  }

  /** Add a game object to the game */
  public GameObject add(Object... components) {
    GameObject newGameObject = root.add(components);
    // forward add events from the new game object to game event manager
    newGameObject.on("add", event -> eventManager.trigger(event.getName(), event.getParameters()));
    return newGameObject;
  }

  /** Update all game objects */
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

  public void checkFrame() {
    // checkObj(game.root);
    root.getChildren().forEach(this::checkObj);
  }

  public void checkObj(GameObject obj) {
    AreaComponent aobj = obj.getComponent(AreaComponent.class);
    if (aobj != null && !obj.isPaused()) {
      // TODO: only update worldArea if transform changed
      Rect area = aobj.worldArea();

      for (GameObject other : root.getChildren()) {
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

  public void on(String event, EventListener listener) {
    eventManager.addEventListener(event, listener);
  }


  public void trigger(String event, Object...params) {
    eventManager.trigger(event, Arrays.asList(params));
  }

  /** Update all game objects */
  public void onUpdate(String tag, EventListener eventListener) {
    on("update", event -> {
      GameObject gameObject = event.getParameter(0);
      // only propagate event when the object has the required tag
      if (gameObject.is(tag)) {
        eventListener.onEvent(event);
      }
    });

  }


}