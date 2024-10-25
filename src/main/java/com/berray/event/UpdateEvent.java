package com.berray.event;

import java.util.List;

/** Event fired each frame to update the scene graph. */
public class UpdateEvent extends Event {
  public static final String EVENT_NAME = "update";
  public UpdateEvent(List<Object> parameters) {
    super(EVENT_NAME, parameters);
  }

  public float getFrametime() {
    return getParameter(1);
  }

}
