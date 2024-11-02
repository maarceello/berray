package com.berray.event;

import com.berray.GameObject;
import com.berray.math.Collision;

import java.util.List;

public class PhysicsCollideEndEvent extends Event {
  public static final String EVENT_NAME = "collideEnd";

  public PhysicsCollideEndEvent(String name, List<Object> parameters) {
    super(EVENT_NAME, parameters);
  }

  /** Object Id of collision partner. */
  public Integer getCollisionPartnerId() {
    return getParameter(1);
  }
}
