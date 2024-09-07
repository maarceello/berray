package com.berray.event;

import java.util.List;
import java.util.function.Supplier;

public class Event {
  /** Event name */
  private final String name;

  /** Variable list of event parameters. */
  private final List<Object> parameters;

  public Event(String name, List<Object> parameters) {
    this.name = name;
    this.parameters = parameters;
  }

  public String getName() {
    return name;
  }

  public List<Object> getParameters() {
    return parameters;
  }

  @SuppressWarnings("unchecked")
  public <E> E getParameter(int i) {
    Object value = parameters.get(i);
    if (value == null) {
      return null;
    }
    // if the parameter value should be calculated
    if (value instanceof Supplier) {
      // get calculated value
      E calculatedValue = ((Supplier<E>) value).get();
      // and replace the supplier with the calculated value
      parameters.set(i, calculatedValue);
      return calculatedValue;
    }
    return (E) value;
  }
}
