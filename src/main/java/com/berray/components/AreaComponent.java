package com.berray.components;

import com.berray.GameObject;
import com.berray.event.Event;
import com.berray.math.Collision;
import com.berray.math.Rect;

import java.util.*;

public class AreaComponent extends Component {

  /**
   * List of objects this object is already colliding with.
   */
  private Map<Integer, Collision> colliding = new HashMap<>();
  private Set<Integer> collidingThisFrame = new HashSet<>();

  public AreaComponent(Rect shape) {
    super("area");
  }


  @Override
  public void add(GameObject gameObject) {
    super.add(gameObject);
    gameObject.on("hover", this::onHover);
    gameObject.on("collideUpdate", this::onCollideUpdate);
    gameObject.on("update", this::onUpdate);

    gameObject.registerGetter("worldArea", this::worldArea);
  }

  public void onHover(Event event) {

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
    Iterator<Integer> iterator = collidingThisFrame.iterator();
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
