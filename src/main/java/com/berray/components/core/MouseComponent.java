package com.berray.components.core;

import com.berray.GameObject;
import com.berray.event.CoreEvents;
import com.berray.event.Event;
import com.berray.event.MouseEvent;
import com.berray.event.UpdateEvent;
import com.berray.math.Matrix4;
import com.berray.math.Vec2;
import com.berray.math.Vec3;

import static com.berray.event.CoreEvents.SCENE_GRAPH_ADDED;

/**
 * Component to supply hoverEnter and hoverLeave events and drag events.
 * Note: needs "area" component so the component can check if the mouse cursor is over this game object
 */
public class MouseComponent extends Component {
  private boolean hoveredThisFrame = false;
  private boolean hoveredLastFrame = false;
  private boolean pressed = false;
  private boolean dragging = false;
  /**
   * Position, where the mouse button was initially pressed down.
   */
  private Vec2 mousePressedPosition;

  public MouseComponent() {
    super("mouse", "area");
  }

  @Override
  public void add(GameObject gameObject) {
    super.add(gameObject);

    on(SCENE_GRAPH_ADDED, this::processSceneGraphAdded);
  }

  private void processSceneGraphAdded(Event e) {
    onGame(CoreEvents.MOUSE_MOVE, this::processMouseMove);
    onGame(CoreEvents.MOUSE_PRESS, this::processMousePress);
    onGame(CoreEvents.MOUSE_RELEASE, this::processMouseRelease);
    on(CoreEvents.UPDATE, this::processUpdateEvent);
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

  private void processMouseRelease(MouseEvent event) {
    // if the mouse is released and it was pressed above this object,
    // always send the release event.
    if (pressed) {
      Vec2 mousePos = event.getWindowPos();

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
      mousePressedPosition = null;
    }
  }

  private void processMousePress(MouseEvent event) {
    Vec2 mousePos = event.getWindowPos();
    if (gameObject.getBoundingBox().contains(mousePos)) {
      emitMousePressEvent(mousePos);
      this.pressed = true;
      this.mousePressedPosition = mousePos;
    }
  }

  private void processMouseMove(MouseEvent event) {
    Vec2 mousePos = event.getWindowPos();
    if (gameObject.getBoundingBox().contains(mousePos)) {
      hoveredThisFrame = true;
      emitHoverEvent(mousePos);
    }

    if (pressed) {
      // moving the mouse while pressing the buttons means dragging the mouse
      Vec2 delta = mousePressedPosition.sub(mousePos);
      // dragging only starts then the mouse actually moves while the button is pressed
      if (delta.lengthSquared() > 0.0f) {
        Vec2 localPos = worldPosToLocalPos(mousePos);
        if (!dragging) {
          emitDragStartEvent(mousePos, localPos);
          dragging = true;
        }
        emitDraggingEvent(mousePos, localPos);
      }
    }
  }

  private void processUpdateEvent(UpdateEvent event) {
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
   * Fired when the mouse button is pressed and released over the game object.
   *
   * @type emit-event
   */
  private void emitMouseClickEvent(Vec2 mousePos, Vec2 localPos) {
    gameObject.trigger(CoreEvents.MOUSE_CLICK, gameObject, localPos, mousePos);
  }

  /**
   * Fired when the mouse button is released.
   *
   * @type emit-event
   */
  private void emitMouseReleaseEvent(Vec2 mousePos, Vec2 localPos) {
    gameObject.trigger(CoreEvents.MOUSE_RELEASE, gameObject, localPos, mousePos);
  }

  /**
   * Fired when the mouse button is pressed down.
   *
   * @type emit-event
   */
  private void emitMousePressEvent(Vec2 mousePos) {
    gameObject.trigger(CoreEvents.MOUSE_PRESS, gameObject, worldPosToLocalPos(mousePos), mousePos);
  }


  /**
   * Fired then the game object dragging is started.
   *
   * @type emit-event
   */
  private void emitDragStartEvent(Vec2 mousePos, Vec2 localPos) {
    gameObject.trigger(CoreEvents.DRAG_START, gameObject, localPos, mousePos);
  }

  /**
   * Fired each frame the mouse is moved and the dragging operation is active.
   *
   * @type emit-event
   */
  private void emitDraggingEvent(Vec2 mousePos, Vec2 localPos) {
    gameObject.trigger(CoreEvents.DRAGGING, gameObject, localPos, mousePos);
  }

  /**
   * Fired when the dragging of the object is finished.
   *
   * @type emit-event
   */
  private void emitDragFinishEvent(Vec2 mousePos, Vec2 localPos) {
    gameObject.trigger(CoreEvents.DRAG_FINISH, gameObject, localPos, mousePos);
  }

  /**
   * Fired then the mouse is hovered over the object. Note that there may also be be a mouse press and/or a dragging operation.
   *
   * @type emit-event
   */
  private void emitHoverEvent(Vec2 mousePos) {
    gameObject.trigger(CoreEvents.HOVER, gameObject, worldPosToLocalPos(mousePos), mousePos);
  }

  /**
   * Fired when the mouse cursor enters the bounding box of the game object.
   *
   * @type emit-event
   */
  private void emitHoverEnterEvent() {
    gameObject.trigger(CoreEvents.HOVER_ENTER, gameObject);
  }

  /**
   * Fired when the mouse cursor leaves the bounding box of the game object.
   *
   * @type emit-event
   */
  private void emitHoverLeaveEvent() {
    gameObject.trigger(CoreEvents.HOVER_LEAVE, gameObject);
  }

  /**
   * creates a new mouse() component.
   */
  public static MouseComponent mouse() {
    return new MouseComponent();
  }
}
