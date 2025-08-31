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
    // sort objects by z index (high z index first) so the uppermost object is hovered and hit first
    gameObjectList.sort((a,b) -> -Integer.compare(a.getZ(), b .getZ()));
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
        if (event.isConsumed()) {
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

    // process of calculating hovered objects
    // 0. get list objects which were hovered over last frame
    // 1. get list of (registered) objects where the bounding box contains the mouse cursor
    // 2. notify new objects with hover enter (note that these objects are sorted by z index descending)
    //    a. iterate over list of currently hovered objects
    //    b. is the object in the list of previously hovered objects (step 0)
    //    c. no, send the current object a hover enter event
    //    d. send the current object a hover event. if the current object marks the event as consumed, remove all
    //       remaining objects from the list of currently hovered objects and stop the loop
    // 3. check which objects are not hovered anymore
    //    a. iterate over the list of hovered object from last frame (step 0)
    //    b. if the object is not in the list of hovered objects from this frame (step 1, possibly cut in step 2), remove it from the list
    //       of hovered object from last frame and send a hover leave event


    // step 0: get list objects which were hovered over last frame
    Set<GameObject> hoveredLastFrame = this.hoveredObjects;

    // 1. get list of (registered) objects where the bounding box contains the mouse cursor
    Set<GameObject> thisFrameHoveredObjects = new LinkedHashSet<>();
    for (GameObject gameObject : gameObjectList) {
      if (gameObject.getBoundingBox().contains(mousePos)) {
        thisFrameHoveredObjects.add(gameObject);
      }
    }

    // 2. notify new objects with hover enter (note that these objects are sorted by z index descending)
    //    a. iterate over list of currently hovered objects
    for (Iterator<GameObject> iterator = thisFrameHoveredObjects.iterator(); iterator.hasNext(); ) {
        GameObject gameObject = iterator.next();
        //    b. is the object in the list of previously hovered objects (step 0)
        if (!hoveredLastFrame.contains(gameObject)) {
            //    c. no, send the current object a hover enter event
            emitHoverEnterEvent(gameObject, mousePos);
        }
        //    d. send the current object a hover event. if the current object marks the event as consumed, remove all
        //       remaining objects from the list of currently hovered objects and stop the loop
        boolean consumed = emitHoverEvent(gameObject, mousePos);
        if (consumed) {
          // drain the iterator, removing all remaining elements
          while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
          }
          // as the iterator is drained, the loop is stopped automatically (iterator.hasNext() returns false)
        }
    }


    // 3. check which objects are not hovered anymore
    //    a. iterate over the list of hovered object from last frame (step 0)
    Iterator<GameObject> hoveredLastFrameIterator = hoveredLastFrame.iterator();
    while ( hoveredLastFrameIterator.hasNext()) {
      GameObject object = hoveredLastFrameIterator.next();
      //    b. if the object is not in the list of hovered objects from this frame (step 1, possibly cut in step 2), remove it from the list
      //       of hovered object from last frame and send a hover leave event
      if (!thisFrameHoveredObjects.contains(object)) {
        hoveredLastFrameIterator.remove();
        emitHoverLeaveEvent(object, mousePos);
      }
    }

    // remember the currently hovered objects for next frame
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

      // only trigger click when the release is also over the game object
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
    return event.isConsumed();
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
   * @return true when the event is consumed, false otherwise
   * @type emit-event
   */
  private boolean emitHoverEvent(GameObject gameObject, Vec2 mousePos) {
    Vec2 localPos = worldPosToLocalPos(gameObject, mousePos);
    MouseEvent event = MouseEvent.createMouseEvent(CoreEvents.HOVER, Arrays.asList(gameObject, mousePos, localPos));
    gameObject.trigger(event);
    return event.isConsumed();
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
