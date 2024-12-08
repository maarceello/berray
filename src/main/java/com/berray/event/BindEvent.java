package com.berray.event;

import java.util.List;

/**
 * Event fired when an data object is bound to a gui element.
 *
 * @type event
 */
public class BindEvent extends Event {
  public static final String EVENT_NAME_BIND = "bind";
  public static final String EVENT_NAME_UNBIND = "unbind";

  public BindEvent(String name, List<Object> parameters) {
    super(name, parameters);
  }

  public Object getBindTarget() {
    return getParameter(1);
  }
}
