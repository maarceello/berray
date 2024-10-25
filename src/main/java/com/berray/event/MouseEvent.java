package com.berray.event;

import com.berray.math.Vec2;

import java.util.List;

/**
 * Base class for all mouse events.
 * <p>
 * Some notes for mouse event parameter.
 * <ul>
 *   <li>0. game object which sends the event</li>
 *   <li>1. window mouse position</li>
 *   <li>2. local mouse position (null for global event)</li>
 *   <li>3. parameters for specific Mouse event</li>
 * </ul>
 */
public class MouseEvent extends Event {
  public static final String EVENT_NAME_MOUSE_CLICK = "mouseClick";
  public static final String EVENT_NAME_MOUSE_RELEASE = "mouseRelease";
  public static final String EVENT_NAME_MOUSE_PRESS = "mousePress";
  public static final String EVENT_NAME_MOUSE_MOVE = "mouseMove";
  public static final String EVENT_NAME_DRAG_START = "dragStart";
  public static final String EVENT_NAME_DRAGGING = "dragging";
  public static final String EVENT_NAME_DRAG_FINISH = "dragFinish";
  public static final String EVENT_NAME_HOVER = "hover";
  public static final String EVENT_NAME_HOVER_ENTER = "hoverEnter";
  public static final String EVENT_NAME_HOVER_LEAVE = "hoverLeave";

  public MouseEvent(String name, List<Object> parameters) {
    super(name, parameters);
  }

  public Vec2 getWindowPos() {
    return getParameter(1);
  }

  public Vec2 getGameObjectPos() {
    return getParameter(2);
  }
}
