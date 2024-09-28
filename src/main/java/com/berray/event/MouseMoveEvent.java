package com.berray.event;

import com.berray.math.Vec2;

import java.util.List;

public class MouseMoveEvent extends Event {
  public MouseMoveEvent(String name, List<Object> parameters) {
    super(name, parameters);
  }

  public Vec2 getPosition() {
    return getParameter(0);
  }
}
