package com.berray.components.core;

import com.berray.GameObject;
import com.berray.event.Event;
import com.berray.math.Matrix4;
import com.berray.math.Vec2;
import com.berray.math.Vec3;

/**
 * Component to supply hoverEnter and hoverLeave events and drag events.
 * Note: needs "area" component so the component can check if the mouse cursor is over this game object
 *
 * @precondition area to check if the mouse cursor is over the component
 * @trigger hover when the curser is moved over the component. Parameter: GameObject which is hovered, Vec2 position
 * of the mouse in component coordinate system, Vec2 position of the mouse in world coordinate system
 * @trigger hoverEnter when the curser is enters the component. Parameter: GameObject which is hovered, Vec2 position
 * of the mouse in component coordinate system, Vec2 position of the mouse in world coordinate system
 * @trigger hoverLeave when the curser is leaves the component. Parameter: GameObject which is hovered, Vec2 position
 * of the mouse in component coordinate system, Vec2 position of the mouse in world coordinate system
 * @trigger mousePress when the left mouse button is pressed down while the mouse curser is over the component.
 * Parameter: GameObject which is clicked, Vec2 position of the mouse in component coordinate system,
 * Vec2 position of the mouse in world coordinate system
 * @trigger mouseRelease when the left mouse button is released when is was previously pressed down over the component.
 * Note that the event is even sent when the mouse left the component.
 * Parameter: GameObject which is clicked, Vec2 position of the mouse in component coordinate system,
 * Vec2 position of the mouse in world coordinate system
 * @trigger mouseCLick when the left mouse button is pressed and released over the component.
 * Parameter: GameObject which is clicked, Vec2 position of the mouse in component coordinate system,
 * Vec2 position of the mouse in world coordinate system
 */
public class MouseComponent extends Component {
  private boolean hoveredThisFrame = false;
  private boolean hoveredLastFrame = false;
  private boolean pressed = false;

  public MouseComponent() {
    super("mouse", "area");
  }

  @Override
  public void add(GameObject gameObject) {
    super.add(gameObject);

    onGame("mouseMove", this::processMouseMove);
    onGame("mousePress", this::processMousePress);
    onGame("mouseRelease", this::processMouseRelease);
    on("update", this::processUpdateEvent);
    registerGetter("hovered", this::isHovered);
  }

  private void processMouseRelease(Event event) {
    // if the mouse is released and it was pressed above this object,
    // always send the release event.
    if (pressed) {
      Vec2 mousePos = event.getParameter(0);

      Vec2 localPos = worldPosToLocalPos(mousePos);
      gameObject.trigger("mouseRelease", gameObject, localPos, mousePos);

      // only trigger click wenn the release is also over the game object
      if (gameObject.getBoundingBox().contains(mousePos)) {
        gameObject.trigger("mouseClick", gameObject, localPos, mousePos);
      }
    }
    this.pressed = false;
  }

  private Vec2 worldPosToLocalPos(Vec2 mousePos) {
    Matrix4 inverseTransform = gameObject.getWorldTransform().inverse();
    Vec3 localVec3 = inverseTransform.multiply(mousePos.getX(), mousePos.getY(), 0);
    return new Vec2(localVec3.getX(), localVec3.getY());
  }

  private void processMousePress(Event event) {
    Vec2 mousePos = event.getParameter(0);
    if (gameObject.getBoundingBox().contains(mousePos)) {
      gameObject.trigger("mousePress", gameObject, worldPosToLocalPos(mousePos), mousePos);
      this.pressed = true;
    }

  }

  private void processMouseMove(Event event) {
    Vec2 mousePos = event.getParameter(0);
    if (gameObject.getBoundingBox().contains(mousePos)) {
      hoveredThisFrame = true;
      gameObject.trigger("hover", this, worldPosToLocalPos(mousePos), mousePos);
    }
  }

  /**
   * Returns if the mouse cursor is over the component (hovered).
   *
   * @type property
   */
  public boolean isHovered() {
    return hoveredThisFrame;
  }

  private void processUpdateEvent(Event event) {
    if (!hoveredLastFrame && hoveredThisFrame) {
      gameObject.trigger("hoverEnter", gameObject);
    }

    if (hoveredLastFrame && !hoveredThisFrame) {
      gameObject.trigger("hoverLeave", gameObject);
    }

    hoveredLastFrame = hoveredThisFrame;
    hoveredThisFrame = false;
  }


  /**
   * creates a new mouse() component.
   */
  public static MouseComponent mouse() {
    return new MouseComponent();
  }
}
