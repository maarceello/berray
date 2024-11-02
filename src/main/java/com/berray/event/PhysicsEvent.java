package com.berray.event;

import com.berray.GameObject;

import java.util.List;

/** Physics Event without additional parameter. */
public class PhysicsEvent extends Event {
  // Note: maybe create an additional event class with property 'ceiling'.
  public static final String EVENT_NAME_HEADBUTT = "headbutt";
  // Note: maybe create an additional event class with property 'plattform'.
  public static final String EVENT_NAME_GROUND = "ground";
  public static final String EVENT_NAME_FALL = "fall";
  public static final String EVENT_NAME_FALL_OFF = "fallOff";


  public PhysicsEvent(String name, List<Object> parameters) {
    super(name, parameters);
  }

  public GameObject getOther() {
    return getParameter(1);
  }
}
