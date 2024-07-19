package com.berray.event;

import java.util.List;

public class EventListeners {
  private List<EventListener> eventListener;

  public void addEventListener(EventListener eventListener) {
    this.eventListener.add(eventListener);
  }
}
