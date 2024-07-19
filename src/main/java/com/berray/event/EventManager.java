package com.berray.event;

import java.util.Map;

/** stores event callbacks and triggers events. */
public class EventManager {

  /** event-name -> registered event listeners. */
  private Map<String, EventListeners> eventListenersMap;


  public void addEventListener(String event, EventListener eventListener) {
    eventListenersMap.computeIfAbsent(event, e -> new EventListeners()).addEventListener(eventListener);
  }

}
