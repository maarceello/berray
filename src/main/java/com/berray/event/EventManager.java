package com.berray.event;

import com.berray.GameObject;

import java.util.List;
import java.util.Map;

/** stores event callbacks and triggers events. */
public class EventManager {

  /** event-name -> registered event listeners. */
  private Map<String, EventListeners> eventListenersMap;


  public void addEventListener(String event, EventListener eventListener) {
    eventListenersMap.computeIfAbsent(event, e -> new EventListeners()).addEventListener(eventListener);
  }

  public void trigger(String eventName, List<Object> params) {
    EventListeners listeners = eventListenersMap.get(eventName);
    if (listeners != null) {
      listeners.trigger(new Event(eventName, params));
    }
  }
}
