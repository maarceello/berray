package com.berray;

import com.berray.components.core.Component;

/**
 * Wrapper class around the component to record the stack trace where the component was added first to the game object.
 */
public class ComponentHolder {
  private final Component component;
  private final Exception whereAdded;

  public ComponentHolder(Component component) {
    this.component = component;
    this.whereAdded = new Exception("component was added here");
  }

  public Component getComponent() {
    return component;
  }

  public Exception getWhereAdded() {
    return whereAdded;
  }
}
