package com.berray.components.core;

import com.berray.GameObject;
import com.berray.event.Event;
import com.berray.math.Matrix4;
import com.berray.math.Vec2;
import com.berray.math.Vec3;

/**
 * Component to supply hoverEnter and hoverLeave events and drag events.
 * Note: needs "area" component so the component can check if the mouse cursor is over this game object
 */
public class MouseComponent extends Component {
  private boolean hoveredThisFrame = false;
  private boolean hoveredLastFrame = false;
  private boolean pressed = false;
  private boolean dragging = false;

  public MouseComponent() {
    super("mouse", "area");
  }

  @Override
  public void add(GameObject gameObject) {
    super.add(gameObject);

    on("sceneGraphAdded", this::processSceneGraphAdded);
  }

  private void processSceneGraphAdded(Event e) {
    onGame("mouseMove", this::processMouseMove);
    onGame("mousePress", this::processMousePress);
    onGame("mouseRelease", this::processMouseRelease);
    on("update", this::processUpdateEvent);
    registerGetter("hovered", this::isHovered);
  }

  /**
   * Returns if the mouse cursor is over the component (hovered).
   *
   * @type property
   */
  public boolean isHovered() {
    return hoveredThisFrame;
  }

  private void processMouseRelease(Event event) {
    // if the mouse is released and it was pressed above this object,
    // always send the release event.
    if (pressed) {
      Vec2 mousePos = event.getParameter(0);

      Vec2 localPos = worldPosToLocalPos(mousePos);
      emitMouseReleaseEvent(mousePos, localPos);

      // only trigger click wenn the release is also over the game object
      if (gameObject.getBoundingBox().contains(mousePos)) {
        emitMouseClickEvent(mousePos, localPos);
      }
      this.pressed = false;

      if (dragging) {
        emitDragFinishEvent(mousePos, localPos);
        dragging = false;
      }
    }
  }

  private void processMousePress(Event event) {
    Vec2 mousePos = event.getParameter(0);
    if (gameObject.getBoundingBox().contains(mousePos)) {
      emitMousePressEvent(mousePos);
      this.pressed = true;
    }

  }

  private void processMouseMove(Event event) {
    Vec2 mousePos = event.getParameter(0);
    if (gameObject.getBoundingBox().contains(mousePos)) {
      hoveredThisFrame = true;
      emitHoverEvent(mousePos);
    }

    if (pressed) {
      // moving the mouse while pressing the buttons means dragging the mouse

      Vec2 localPos = worldPosToLocalPos(mousePos);
      if (!dragging) {
        emitDragStartEvent(mousePos, localPos);
        dragging = true;
      }
      emitDraggingEvent(mousePos, localPos);
    }
  }

  private void processUpdateEvent(Event event) {
    if (!hoveredLastFrame && hoveredThisFrame) {
      emitHoverEnterEvent();
    }

    if (hoveredLastFrame && !hoveredThisFrame) {
      emitHoverLeaveEvent();
    }

    hoveredLastFrame = hoveredThisFrame;
    hoveredThisFrame = false;
  }

  private Vec2 worldPosToLocalPos(Vec2 mousePos) {
    Matrix4 inverseTransform = gameObject.getWorldTransform().inverse();
    Vec3 localVec3 = inverseTransform.multiply(mousePos.getX(), mousePos.getY(), 0);
    return new Vec2(localVec3.getX(), localVec3.getY());
  }

  /**
   * Fired when the dragging of the object is finished.
   *
   * @type emit-event
   */
  private void emitDragFinishEvent(Vec2 mousePos, Vec2 localPos) {
    gameObject.trigger("dragFinish", gameObject, localPos, mousePos);
  }

  /**
   * Fired when the mouse button is pressed and released over the game object.
   *
   * @type emit-event
   */
  private void emitMouseClickEvent(Vec2 mousePos, Vec2 localPos) {
    gameObject.trigger("mouseClick", gameObject, localPos, mousePos);
  }

  /**
   * Fired when the mouse button is released.
   *
   * @type emit-event
   */
  private void emitMouseReleaseEvent(Vec2 mousePos, Vec2 localPos) {
    gameObject.trigger("mouseRelease", gameObject, localPos, mousePos);
  }

  /**
   * Fired when the mouse button is pressed down.
   *
   * @type emit-event
   */
  private void emitMousePressEvent(Vec2 mousePos) {
    gameObject.trigger("mousePress", gameObject, worldPosToLocalPos(mousePos), mousePos);
  }

  /**
   * Fired each frame the mouse is moved and the dragging operation is active.
   *
   * @type emit-event
   */
  private void emitDraggingEvent(Vec2 mousePos, Vec2 localPos) {
    gameObject.trigger("dragging", gameObject, localPos, mousePos);
  }

  /**
   * Fired then the game object dragging is started.
   *
   * @type emit-event
   */
  private void emitDragStartEvent(Vec2 mousePos, Vec2 localPos) {
    gameObject.trigger("dragStart", gameObject, localPos, mousePos);
  }

  /**
   * Fired then the mouse is hovered over the object. Note that there may also be be a mouse press and/or a dragging operation.
   *
   * @type emit-event
   */
  private void emitHoverEvent(Vec2 mousePos) {
    gameObject.trigger("hover", gameObject, worldPosToLocalPos(mousePos), mousePos);
  }


  /**
   * Fired when the mouse cursor leaves the bounding box of the game object.
   *
   * @type emit-event
   */
  private void emitHoverLeaveEvent() {
    gameObject.trigger("hoverLeave", gameObject);
  }

  /**
   * Fired when the mouse cursor enters the bounding box of the game object.
   *
   * @type emit-event
   */
  private void emitHoverEnterEvent() {
    gameObject.trigger("hoverEnter", gameObject);
  }


  /**
   * creates a new mouse() component.
   */
  public static MouseComponent mouse() {
    return new MouseComponent();
  }
}
