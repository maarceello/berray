package com.berray.components.core;

import com.berray.GameObject;
import com.berray.event.Event;
import com.berray.math.Vec2;

/** Component to supply hoverEnter and hoverLeave events and drag events.
 * Note: needs "area" component so the component can check if the mouse cursor is over this game object
 * */
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
      gameObject.trigger("mouseRelease", gameObject, mousePos);
    }
    this.pressed = false;
  }

  private void processMousePress(Event event) {
    Vec2 mousePos = event.getParameter(0);
    if (gameObject.getBoundingBox().contains(mousePos)) {
      // TODO: calculate pos in local object coordinates
      gameObject.trigger("mousePress", gameObject, mousePos);
      this.pressed = true;
    }

  }

  private void processMouseMove(Event event) {
    Vec2 mousePos = event.getParameter(0);
    if (gameObject.getBoundingBox().contains(mousePos)) {
      hoveredThisFrame = true;
      // TODO: calculate pos in local object coordinates
      gameObject.trigger("hover", this, mousePos);
    }
  }

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


  public static MouseComponent mouse() {
    return new MouseComponent();
  }
}
