package com.berray.event;

import com.berray.GameObject;
import com.berray.math.Collision;

import java.util.List;

public class PhysicsCollideEndEvent extends Event {
  public static final String EVENT_NAME = "collideEnd";

  public PhysicsCollideEndEvent(String name, List<Object> parameters) {
    super(EVENT_NAME, parameters);
  }

  /** Object which does not collide anymore. */
  public Collision getCollision() {
    return getParameter(1);
  }
}
