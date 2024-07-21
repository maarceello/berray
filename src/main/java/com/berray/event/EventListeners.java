package com.berray.event;

import java.util.ArrayList;
import java.util.List;

public class EventListeners {
  private List<EventListener> eventListener = new ArrayList<>();

  public void addEventListener(EventListener eventListener) {
    this.eventListener.add(eventListener);
  }

  public void trigger(Event event) {
    eventListener.forEach(listener -> listener.onEvent(event));
  }
}
