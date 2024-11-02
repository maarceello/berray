package com.berray.event;

import com.berray.GameObject;
import com.berray.math.Collision;

import java.util.List;

public class PhysicsCollideEvent extends Event {
  public static final String EVENT_NAME = "collide";

  public PhysicsCollideEvent(String name, List<Object> parameters) {
    super(EVENT_NAME, parameters);
  }

  public Collision getCollision() {
    return getParameter(1);
  }

  public GameObject getCollisionPartner() {
    return getParameter(2);
  }

}
