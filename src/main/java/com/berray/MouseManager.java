package com.berray;


import com.berray.event.CoreEvents;
import com.berray.event.MouseEvent;
import com.berray.event.MouseWheelEvent;
import com.berray.math.Matrix4;
import com.berray.math.Vec2;
import com.berray.math.Vec3;

import java.util.*;

/**
 * Class to process mouse events and send them to interested parties.
 */
public class MouseManager {
  private boolean pressed = false;
  private boolean dragging = false;
  /**
   * Position, where the mouse button was initially pressed down.
   */
  private Vec2 mousePressedPosition;

  private List<GameObject> gameObjectList = new ArrayList<>();
  private GameObject pressedGameObject;

  private Set<GameObject> hoveredObjects = new HashSet<>();

  public void registerGameObject(GameObject gameObject) {
    if (!gameObject.is("mouse")) {
      throw new IllegalStateException();
    }
    gameObjectList.add(gameObject);
    // todo: sort game objects
  }

  public void removeGameObject(GameObject gameObject) {
    gameObjectList.remove(gameObject);
  }



  /**
   * Registers the mouse listeners to the game.
   */
  public void registerListeners(Game game) {
    game.on(CoreEvents.MOUSE_MOVE, this::processMouseMove, this);
    game.on(CoreEvents.MOUSE_PRESS, this::processMousePress, this);
    game.on(CoreEvents.MOUSE_RELEASE, this::processMouseRelease, this);
    game.on(CoreEvents.MOUSE_WHEEL_MOVE, this::processMouseWheel, this);
  }

  private void processMouseWheel(MouseWheelEvent event) {
    Vec2 mousePos = event.getWindowPos();

    for (GameObject gameObject : gameObjectList) {
      if (gameObject.getBoundingBox().contains(mousePos)) {
        emitMouseWheelEvent(gameObject, mousePos, event.getWheelDelta());
        if (event.isProcessed()) {
          break;
        }
      }
    }
  }

  private void processMousePress(MouseEvent event) {
    Vec2 mousePos = event.getWindowPos();
    for (GameObject gameObject : gameObjectList) {
      if (gameObject.getBoundingBox().contains(mousePos)) {
        boolean processed = emitMousePressEvent(gameObject, mousePos);
        this.pressedGameObject = gameObject;
        this.pressed = true;
        this.mousePressedPosition = mousePos;
        if (processed) {
          break;
        }
      }
    }
  }

  private void processMouseMove(MouseEvent event) {
    Vec2 mousePos = event.getWindowPos();

    // calculate objects which are hovered this frame
    Set<GameObject> thisFrameHoveredObjects = new HashSet<>();
    for (GameObject gameObject : gameObjectList) {
      if (gameObject.getBoundingBox().contains(mousePos)) {
        thisFrameHoveredObjects.add(gameObject);
      }
    }

    Set<GameObject> hoveredLastFrame = this.hoveredObjects;
    // remove all objects which were hovered last frame and are not hovered this frame. Send these game objects the hoverLeave event
    Iterator<GameObject> hoveredLastFrameIterator = hoveredLastFrame.iterator();
    while ( hoveredLastFrameIterator.hasNext()) {
      GameObject object = hoveredLastFrameIterator.next();
      if (!thisFrameHoveredObjects.contains(object)) {
        emitHoverLeaveEvent(object, mousePos);
        hoveredLastFrameIterator.remove();
      }
    }

    // send the hovered objects a 'hover' event. When the object was not hovered last frame, send a 'hoverEnter' event
    for (GameObject gameObject : thisFrameHoveredObjects) {
      if (!hoveredLastFrame.contains(gameObject)) {
        emitHoverEnterEvent(gameObject, mousePos);
      }
      emitHoverEvent(gameObject, mousePos);
    }
    // remember the hovered objects for next frame
    this.hoveredObjects = thisFrameHoveredObjects;

    // are we dragging at the moment?
    if (pressed && pressedGameObject != null) {
      // moving the mouse while pressing the buttons means dragging the mouse
      Vec2 delta = mousePressedPosition.sub(mousePos);
      // dragging only starts then the mouse actually moves while the button is pressed
      if (delta.lengthSquared() > 0.0f) {
        Vec2 localPos = worldPosToLocalPos(pressedGameObject, mousePos);
        if (!dragging) {
          emitDragStartEvent(pressedGameObject, mousePos, localPos);
          dragging = true;
        }
        emitDraggingEvent(pressedGameObject, mousePos, localPos);
      }
    }
  }

  private void processMouseRelease(MouseEvent event) {
    // if the mouse is released and it was pressed above an object,
    // always send the release event.
    if (pressed && pressedGameObject != null) {
      Vec2 mousePos = event.getWindowPos();

      Vec2 localPos = worldPosToLocalPos(pressedGameObject, mousePos);
      emitMouseReleaseEvent(pressedGameObject, mousePos, localPos);

      // only trigger click wenn the release is also over the game object
      if (pressedGameObject.getBoundingBox().contains(mousePos)) {
        emitMouseClickEvent(pressedGameObject, mousePos, localPos);
      }
      this.pressed = false;

      if (dragging) {
        emitDragFinishEvent(pressedGameObject, mousePos, localPos);
        dragging = false;
      }
      mousePressedPosition = null;
    }
  }

  /**
   * Fired when the mouse wheel was moved while the mouse cursor is over the game object.
   *
   * @type emit-event
   */
  private void emitMouseWheelEvent(GameObject gameObject, Vec2 mousePos, float wheelDelta) {
    Vec2 localPos = worldPosToLocalPos(gameObject, mousePos);
    gameObject.trigger(CoreEvents.MOUSE_WHEEL_MOVE, gameObject, mousePos, localPos, wheelDelta);
  }

  /**
   * Fired when the mouse button is pressed down.
   *
   * @type emit-event
   */
  private boolean emitMousePressEvent(GameObject gameObject, Vec2 mousePos) {
    Vec2 localPos = worldPosToLocalPos(gameObject, mousePos);
    MouseEvent event = MouseEvent.createMouseEvent(MouseEvent.EVENT_NAME_MOUSE_PRESS, Arrays.asList(gameObject, mousePos, localPos));
    gameObject.trigger(event);
    return event.isProcessed();
  }

  /**
   * Fired when the mouse button is released.
   *
   * @type emit-event
   */
  private void emitMouseReleaseEvent(GameObject gameObject, Vec2 mousePos, Vec2 localPos) {
    gameObject.trigger(CoreEvents.MOUSE_RELEASE, gameObject, mousePos, localPos);
  }

  /**
   * Fired when the mouse button is pressed and released over the game object.
   *
   * @type emit-event
   */
  private void emitMouseClickEvent(GameObject gameObject, Vec2 mousePos, Vec2 localPos) {
    gameObject.trigger(CoreEvents.MOUSE_CLICK, gameObject, mousePos, localPos);
  }

  /**
   * Fired then the mouse is hovered over the object. Note that there may also be be a mouse press and/or a dragging operation.
   *
   * @type emit-event
   */
  private void emitHoverEvent(GameObject gameObject, Vec2 mousePos) {
    Vec2 localPos = worldPosToLocalPos(gameObject, mousePos);
    gameObject.trigger(CoreEvents.HOVER, gameObject, mousePos, localPos);
  }

  /**
   * Fired when the mouse cursor enters the bounding box of the game object.
   *
   * @type emit-event
   */
  private void emitHoverEnterEvent(GameObject gameObject, Vec2 mousePos) {
    Vec2 localPos = worldPosToLocalPos(gameObject, mousePos);
    gameObject.trigger(CoreEvents.HOVER_ENTER, gameObject, mousePos, localPos);
  }

  /**
   * Fired when the mouse cursor leaves the bounding box of the game object.
   *
   * @type emit-event
   */
  private void emitHoverLeaveEvent(GameObject gameObject, Vec2 mousePos) {
    Vec2 localPos = worldPosToLocalPos(gameObject, mousePos);
    gameObject.trigger(CoreEvents.HOVER_LEAVE, gameObject, mousePos, localPos);
  }

  /**
   * Fired then the game object dragging is started.
   *
   * @type emit-event
   */
  private void emitDragStartEvent(GameObject gameObject, Vec2 mousePos, Vec2 localPos) {
    gameObject.trigger(CoreEvents.DRAG_START, gameObject, mousePos, localPos);
  }

  /**
   * Fired each frame the mouse is moved and the dragging operation is active.
   *
   * @type emit-event
   */
  private void emitDraggingEvent(GameObject gameObject, Vec2 mousePos, Vec2 localPos) {
    gameObject.trigger(CoreEvents.DRAGGING, gameObject, mousePos, localPos);
  }

  /**
   * Fired when the dragging of the object is finished.
   *
   * @type emit-event
   */
  private void emitDragFinishEvent(GameObject gameObject, Vec2 mousePos, Vec2 localPos) {
    gameObject.trigger(CoreEvents.DRAG_FINISH, gameObject, mousePos, localPos);
  }


  private Vec2 worldPosToLocalPos(GameObject gameObject, Vec2 mousePos) {
    Matrix4 inverseTransform = gameObject.getWorldTransform().inverse();
    Vec3 localVec3 = inverseTransform.multiply(mousePos.getX(), mousePos.getY(), 0);
    return new Vec2(localVec3.getX(), localVec3.getY());
  }
}
