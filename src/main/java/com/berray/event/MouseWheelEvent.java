package com.berray.event;

import java.util.List;

/**
 * Event fired when the mouse wheel was moved.
 *
 * @type event
 * */
public class MouseWheelEvent extends MouseEvent {
  public MouseWheelEvent(String name, List<Object> parameters) {
    super(name, parameters);
  }

  public float getWheelDelta() {
    return getParameter(3);
  }
}
