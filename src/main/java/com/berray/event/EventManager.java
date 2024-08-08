package com.berray.event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores event callbacks and triggers events.
 */
public class EventManager {

  /**
   * event-name -> registered event listeners.
   */
  private Map<String, EventListeners> eventListenersMap = new HashMap<>();

  public void addEventListener(String event, EventListener eventListener) {
    addEventListener(event, eventListener, null);
  }

  public void addEventListener(String event, EventListener eventListener, Object owner) {
    eventListenersMap.computeIfAbsent(event, e -> new EventListeners()).addEventListener(eventListener, owner);
  }

  public void removeListener(Object owner) {
    eventListenersMap.values().forEach(listener -> listener.removeListener(owner));
  }

  public void trigger(String eventName, List<Object> params) {
    EventListeners listeners = eventListenersMap.get(eventName);
    if (listeners != null) {
      listeners.trigger(new Event(eventName, params));
    }
  }

  public void clear() {
    eventListenersMap.clear();
  }
}
