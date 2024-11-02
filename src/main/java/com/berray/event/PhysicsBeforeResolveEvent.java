package com.berray.event;

import com.berray.GameObject;
import com.berray.math.Collision;

import java.util.List;

public class PhysicsBeforeResolveEvent extends Event {
  public static final String EVENT_NAME = "beforePhysicsResolve";

  public PhysicsBeforeResolveEvent(String name, List<Object> parameters) {
    super(name, parameters);
  }

  public Collision getCollision() {
    return getParameter(1);
  }

  public GameObject getCollisionPartner() {
    return getParameter(2);
  }

}
