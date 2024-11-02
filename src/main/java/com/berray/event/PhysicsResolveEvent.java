package com.berray.event;

import com.berray.math.Collision;

import java.util.List;

/** Fired when the collision is resolved. Note: the event is fired for both parties of the collision. */
public class PhysicsResolveEvent extends Event {
  public static final String EVENT_NAME = "physicsResolve";

  public PhysicsResolveEvent(String name, List<Object> parameters) {
    super(EVENT_NAME, parameters);
  }

  public Collision getCollision() {
    return getParameter(1);
  }
}
