package com.berray.components.core;

import com.berray.GameObject;
import com.berray.event.Event;
import com.berray.math.Collision;
import com.berray.math.Rect;

import java.util.*;


/**
 * # AreaComponent
 *
 * {@link AreaComponent#area()} provides collision detection for the game object.
 *
 * # Properties
 *
 * - worldArea (read only) - returns the current absolute positioning of the collision area
 *
 * # Events
 *
 * | Event         | Direction | Description |
 * |---------------|-----------|---|
 * | collideUpdate | consumed  | triggered by collision detection when this object collides with another. |
 * | update        | consumed  | triggered by main update loop. used to keep track of objects which collides this frame and which doesn't collide anymore |
 * | collide       | triggered | triggered when a collision starts |
 * | collideEnd    | triggered | triggered when a collision ends |
 */
public class AreaComponent extends Component {

  /**
   * List of objects this object is already colliding with.
   */
  private Map<Integer, Collision> colliding = new HashMap<>();
  /** List of objects this object is colliding with in the current frame. */
  private Set<Integer> collidingThisFrame = new HashSet<>();

  public AreaComponent(Rect shape) {
    super("area");
  }


  @Override
  public void add(GameObject gameObject) {
    super.add(gameObject);
    gameObject.on("collideUpdate", this::onCollideUpdate);
    gameObject.on("update", this::onUpdate);

    gameObject.registerGetter("worldArea", this::worldArea);
    gameObject.registerAction("isColliding", this::isCollidingWith);
  }

  /**
   * Checks if this GameObeject is colliding with `other` in this frame.
   * Params:
   * - GameObject other */
  public boolean isCollidingWith(List<Object> params) {
    GameObject other = (GameObject) params.get(0);
    return colliding.containsKey(other.getId());
  }

  /**
   * Returns true when this game object is colliding with anything this frame.
   */
  public boolean isColliding() {
    return !colliding.isEmpty();
  }

  public List<Collision> getCollisions() {
    return new ArrayList<>(colliding.values());
  }

  public void onCollideUpdate(Event event) {
    GameObject other = event.getParameter(0);
    Collision collision = event.getParameter(1);

    if (!colliding.containsKey(other.getId())) {
      gameObject.trigger("collide", other, collision);
    }
    if (collision == null) {
      return;
    }

    colliding.put(other.getId(), collision);
    // remember which objects collided this frame
    collidingThisFrame.add(other.getId());
  }

  public void onUpdate(Event event) {
    // check each object which is in the collision set.
    Iterator<Integer> iterator = colliding.keySet().iterator();
    while (iterator.hasNext()) {
      Integer id = iterator.next();
      // does this object collided this frame also?
      if (!collidingThisFrame.contains(id)) {
        // if not, remove it from the set and send the "collideEnd" Event
        gameObject.trigger("collideEnd", colliding.get(id));
        iterator.remove();
      }
    }
    // clear list of collisions for next frame
    collidingThisFrame.clear();
  }

  public Rect worldArea() {
    // TODO: Respect fixed game objects
    // TODO: add using Polygon as area

    Rect rect = gameObject.get("localArea");
    if (rect == null) {
      return null;
    }
    return rect;
  }

  public static AreaComponent area() {
    return new AreaComponent(null);
  }

  public static AreaComponent area(Rect shape) {
    return new AreaComponent(shape);
  }
}
