package com.berray;

import com.berray.components.core.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper class around the component to record the stack trace where the component was added first to the game object.
 */
public class ComponentHolder {
  private final List<Component> components = new ArrayList<>();
  private final Exception whereAdded;

  public ComponentHolder() {
    this.whereAdded = new Exception("component was added here first");
  }

  public void addComponent(Component component) {
    this.components.add(component);
  }

  public List<Component> getComponents() {
    return components;
  }

  public Exception getWhereAdded() {
    return whereAdded;
  }
}
