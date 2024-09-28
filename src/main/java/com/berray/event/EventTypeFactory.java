package com.berray.event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * creates event type specific event objects.
 */
public class EventTypeFactory {

  private static final EventTypeFactory instance = new EventTypeFactory();

  Map<String, BiFunction<String, List<Object>, ? extends Event>> eventTypeCreators = new HashMap<>();

  private EventTypeFactory() {
  }

  public void registerEventType(String type, Function<List<Object>, ? extends Event> creator) {
    eventTypeCreators.put(type, (event, parameter) -> creator.apply(parameter));
  }

  public void registerEventType(String type, BiFunction<String, List<Object>, ? extends Event> creator) {
    eventTypeCreators.put(type, creator);
  }

  public Event createEvent(String event, List<Object> parameters) {
    BiFunction<String, List<Object>, ? extends Event> creator = eventTypeCreators.get(event);
    if (creator != null) {
      return creator.apply(event, parameters);
    }
    return new Event(event, parameters);
  }

  public void copyAll(EventTypeFactory factory) {
    eventTypeCreators.putAll(factory.eventTypeCreators);
  }

  public static EventTypeFactory getInstance() {
    return instance;
  }
}
