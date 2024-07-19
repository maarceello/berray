package com.berray;

import com.berray.components.Component;
import com.berray.event.EventListener;
import com.berray.event.EventManager;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;

public class GameObject {
  private static final AtomicInteger nextComponentId = new AtomicInteger(0);
  private int id;
  /** Custom Tags. */
  private Set<String> tags = new HashSet<>();
  private final Map<Class<?>,Component> components;

  private final Map<String, Supplier<?>> providedMethods = new HashMap<>();
  private final EventManager eventManager = new EventManager();

  public GameObject(int id) {
    this.id = id;
    this.components = new LinkedHashMap<>();
  }

  public void addComponents(Object[] components) {
    for (Object c : components) {
      if (c instanceof String) {
        addTag(c.toString());
      } else if (c instanceof Component) {
        Component component = (Component) c;
        component.setId(nextComponentId.incrementAndGet());
        this.components.put(component.getClass(), component);
      } else {
        throw new IllegalArgumentException("Component of type "+c.getClass()+" not supported. Either add a tag (String) or a component (Component)");
      }
    }
    // notify component that it was added
    for (Object c : components) {
      if (c instanceof Component) {
        Component component = (Component) c;
        component.add(this);
      }
    }

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

  public boolean is(String tag) {
    return tags.contains(tag);
  }

  public void addMethod(String name, Supplier<?> method) {
    providedMethods.put(name, method);
  }

  public <E extends Component> E getComponent(Class<E> type) {
    return (E) components.get(type);
  }

  public void addTag(String tag) {
    tags.add(tag);
  }

  /** add event listener. */
  public void on(String event, EventListener listener) {
    eventManager.addEventListener(event, listener);
  }
}
