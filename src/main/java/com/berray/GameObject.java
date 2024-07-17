package com.berray;

import com.berray.components.Component;

import java.util.LinkedHashMap;
import java.util.Map;

public class GameObject {
  int id;
  private final Map<Class<?>,Component> components;

  public GameObject(int id) {
    this.id = id;
    this.components = new LinkedHashMap<>();
  }

  public void addComponent(Component component) {
    this.components.put(component.getClass(), component);
  }

  public void update() {
  }

  public void draw() {
    for (Component c : components.values()) {
      c.draw(this);
    }
  }

  public <E extends Component> E getComponent(Class<E> type) {
    return (E) components.get(type);
  }



}
