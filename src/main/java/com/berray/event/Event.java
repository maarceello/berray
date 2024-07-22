package com.berray.event;

import java.util.List;

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

  public <E> E getParameter(int i) {
    return (E) parameters.get(i);
  }
}
