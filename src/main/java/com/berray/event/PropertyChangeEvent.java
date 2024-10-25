package com.berray.event;

import java.util.List;

/** Event fired when a bound property changes. */
public class PropertyChangeEvent extends Event {
  public static final String EVENT_NAME = "propertyChange";

  public PropertyChangeEvent(List<Object> parameters) {
    super(EVENT_NAME, parameters);
  }

  public String getPropertyName() {
    return (String) parameters.get(1);
  }

  @SuppressWarnings("unchecked")
  public <E> E getOldValue() {
    return (E) parameters.get(2);
  }

  @SuppressWarnings("unchecked")
  public <E> E getNewValue() {
    return (E) parameters.get(3);
  }
}
