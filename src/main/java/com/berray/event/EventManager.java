package com.berray.event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Stores event callbacks and triggers events.
 */
public class EventManager {

  /**
   * event-name -> registered event listeners.
   */
  private Map<String, EventListeners> eventListenersMap = new HashMap<>();

  public <E extends Event> void addEventListener(String event, EventListener<E> eventListener) {
    addEventListener(event, eventListener, null);
  }

  public <E extends Event> void addEventListener(String event, EventListener<E> eventListener, Object owner) {
    eventListenersMap.computeIfAbsent(event, e -> new EventListeners()).addEventListener(eventListener, owner);
  }

  public void removeListener(Object owner) {
    eventListenersMap.values().forEach(listener -> listener.removeListener(owner));
  }

  public void trigger(String eventName, List<Object> params) {
    EventListeners listeners = eventListenersMap.get(eventName);
    Event event = EventTypeFactory.getInstance().createEvent(eventName, params);
    if (listeners != null) {
      listeners.trigger(event);
    }
  }

  public void clear() {
    eventListenersMap.clear();
  }
}
