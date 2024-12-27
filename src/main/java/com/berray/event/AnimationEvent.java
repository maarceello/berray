package com.berray.event;

import java.util.List;

/**
 * Event fired when an animation starts or ends.
 *
 * @type event
 */
public class AnimationEvent extends Event {
  public static final String EVENT_NAME_ANIMATION_END = "animEnd";
  public static final String EVENT_NAME_ANIMATION_START = "animStart";
  public AnimationEvent(String name, List<Object> parameters) {
    super(name, parameters);
  }

  public String getAnimation() {
    return getParameter(1);
  }
}
