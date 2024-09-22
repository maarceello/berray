package com.berray.event;

import java.util.ArrayList;
import java.util.List;

public class EventListeners {
  private final List<EventListenerWrapper> eventListener = new ArrayList<>();

  public void addEventListener(EventListener<?> eventListener, Object owner) {
    this.eventListener.add(new EventListenerWrapper(eventListener, owner));
  }

  public void  trigger(Event event) {
    eventListener.forEach(listener -> listener.eventListener.onEvent(event));
  }

  public void removeListener(Object owner) {
    eventListener.removeIf(wrapper -> wrapper.owner == owner);
  }

  public static class EventListenerWrapper {
    private final EventListener<Event> eventListener;
    private final Object owner;

    @SuppressWarnings("unchecked")
    public <E extends Event> EventListenerWrapper(EventListener<E> eventListener, Object owner) {
      this.eventListener = (EventListener<Event>) eventListener;
      this.owner = owner;
    }
  }
}
