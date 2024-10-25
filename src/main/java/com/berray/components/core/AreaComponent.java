package com.berray.components.core;

import com.berray.GameObject;
import com.berray.event.CoreEvents;
import com.berray.event.PhysicsCollideUpdateEvent;
import com.berray.event.UpdateEvent;
import com.berray.math.Collision;
import com.berray.math.Rect;
import com.berray.math.Vec2;

import java.util.*;

import static com.berray.event.CoreEvents.PHYSICS_COLLIDE_UPDATE;
import static com.berray.event.CoreEvents.UPDATE;


/**
 * # AreaComponent
 * <p>
 * {@link AreaComponent#area()} provides collision detection for the game object.
 */
public class AreaComponent extends Component {

  /**
   * List of objects this object is already colliding with.
   */
  private Map<Integer, Collision> colliding = new HashMap<>();
  /**
   * List of objects this object is colliding with in the current frame.
   */
  private Set<Integer> collidingThisFrame = new HashSet<>();
  /**
   * List of tags this object should ignore collisions with.
   */
  private Set<String> collisionIgnore = new HashSet<>();

  private Rect collisionArea;
  private float scale = 1.0f;

  public AreaComponent(Rect shape) {
    super("area");
    this.collisionArea = shape;
  }

  @Override
  public void add(GameObject gameObject) {
    super.add(gameObject);
    on(PHYSICS_COLLIDE_UPDATE, this::onCollideUpdate);
    on(UPDATE, this::onUpdate);

    registerAction("isColliding", this::isCollidingWith, IsCollidingWithAction::new);
    registerGetter("collisionIgnore", this::getCollisionIgnore);
    registerGetter("collisions", this::getCollisions);
    registerGetter("localArea", this::getLocalArea);
  }

  /**
   * Scales the collision area by this factor.
   *
   * @type configuration
   */
  public AreaComponent scale(float scale) {
    this.scale = scale;
    return this;
  }

  /**
   * Sets the tags which should be ignored in collision detection.
   *
   * @type configuration
   */
  public AreaComponent ignoreCollisionWith(String... tags) {
    collisionIgnore.addAll(Arrays.asList(tags));
    return this;
  }

  /**
   * Returns collider area in local coordinates.
   *
   * @type property
   */
  public Rect getLocalArea() {
    if (collisionArea != null) {
      return scale == 1.0f ? collisionArea : new Rect(collisionArea.getX() * scale, collisionArea.getY() * scale, collisionArea.getWidth() * scale, collisionArea.getHeight() * scale);
    }
    Vec2 size = gameObject.getOrDefault("size", Vec2.origin());
    return new Rect(0, 0, size.getX() * scale, size.getY() * scale);
  }

  /**
   * Returns the set of tags which should be ignored when doing collision checks.
   *
   * @type property
   */
  public Set<String> getCollisionIgnore() {
    return collisionIgnore;
  }

  /**
   * Returns true when this game object is colliding with anything this frame.
   *
   * @type property
   */
  public boolean isColliding() {
    return !colliding.isEmpty();
  }

  /**
   * Returns current collisions.
   *
   * @type property
   */
  public List<Collision> getCollisions() {
    return new ArrayList<>(colliding.values());
  }

  /**
   * Checks if this GameObject is colliding with `other` in this frame.
   *
   * @type action
   */
  public boolean isCollidingWith(IsCollidingWithAction params) {
    GameObject other = params.getOther();
    return colliding.containsKey(other.getId());
  }


  public void onCollideUpdate(PhysicsCollideUpdateEvent event) {
    GameObject other = event.getCollisionPartner();
    Collision collision = event.getCollision();

    if (!colliding.containsKey(other.getId())) {
      emitCollideEvent(other, collision);
    }
    if (collision == null) {
      return;
    }

    colliding.put(other.getId(), collision);
    // remember which objects collided this frame
    collidingThisFrame.add(other.getId());
  }

  /**
   * Fired each frame for each collision of this object.
   *
   * @type emit-event
   */
  private void emitCollideEvent(GameObject other, Collision collision) {
    gameObject.trigger(CoreEvents.PHYSICS_COLLIDE, gameObject, collision, other);
  }

  /**
   * Fired when a collection ended this frame.
   *
   * @type emit-event
   */
  private void emitCollideEndEvent(Integer id) {
    gameObject.trigger(CoreEvents.PHYSICS_COLLIDE_END, gameObject, colliding.get(id));
  }

  public void onUpdate(UpdateEvent event) {
    // check each object which is in the collision set.
    Iterator<Integer> iterator = colliding.keySet().iterator();
    while (iterator.hasNext()) {
      Integer id = iterator.next();
      // does this object collided this frame also?
      if (!collidingThisFrame.contains(id)) {
        // if not, remove it from the set and send the "collideEnd" Event
        emitCollideEndEvent(id);
        iterator.remove();
      }
    }
    // clear list of collisions for next frame
    collidingThisFrame.clear();
  }

  /**
   * Creates an area component without a collision shape.
   *
   * @type creator
   */
  public static AreaComponent area() {
    return new AreaComponent(null);
  }

  /**
   * Creates an area component without a rectangular collision shape.
   *
   * @type creator
   */
  public static AreaComponent area(Rect shape) {
    return new AreaComponent(shape);
  }

  /**
   * Parameter class for 'isCollidingWith' action.
   */
  private static class IsCollidingWithAction extends Action {

    private IsCollidingWithAction(List<Object> params) {
      super(params);
    }

    public GameObject getOther() {
      return getParameter(0);
    }
  }
}
