package com.berray;

import com.berray.components.Component;
import com.berray.event.EventListener;
import com.berray.event.EventManager;


import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GameObject {
  private static final AtomicInteger nextComponentId = new AtomicInteger(0);
  private static final AtomicInteger nextGameObjectId = new AtomicInteger( 0);
  private int id;
  /** Tags. */
  private Set<String> tags = new HashSet<>();
  /** Components for this game object. */
  private final Map<Class<?>,Component> components;

  /** registered getter methods from components. */
  private final Map<String, Supplier<?>> getterMethods = new HashMap<>();
  /** registered setter methods from components. */
  private final Map<String, Consumer<?>> setterMethods = new HashMap<>();
  /** event manager for game object local event. */
  private final EventManager eventManager = new EventManager();
  private boolean paused;

  /** child game objects. */
  private List<GameObject> children = new LinkedList<>();
  private GameObject parent;


  public GameObject() {
    this.components = new LinkedHashMap<>();
    this.id = nextGameObjectId.incrementAndGet();
  }

  public GameObject(GameObject parent) {
    this();
    this.parent = parent;
  }


  public GameObject add(Object... components) {
    GameObject gameObject = new GameObject(this);
    gameObject.addComponents(components);
    children.add(gameObject);
    // trigger add event for all other interested parties
    trigger("add", this, gameObject);

    return gameObject;
  }

  public List<GameObject> getChildren() {
    return children;
  }

  public void addComponents(Object[] components) {
    for (Object c : components) {
      if (c instanceof String) {
        addTag(c.toString());
      } else if (c instanceof Component) {
        Component component = (Component) c;
        component.setId(nextComponentId.incrementAndGet());
        this.components.put(component.getClass(), component);
        this.tags.add(component.getTag());
        component.setGameObject(this);
      } else {
        throw new IllegalArgumentException("Component of type "+c.getClass()+" not supported. Either add a tag (String) or a component (Component)");
      }
    }
    // notify component that it was added
    for (Object c : components) {
      if (c instanceof Component) {
        Component component = (Component) c;
        // trigger add event for the current component
        component.add(this);
      }
    }
  }

  public GameObject getParent() {
    return parent;
  }

  public void addComponent(Component component) {
    this.components.put(component.getClass(), component);
  }

  public void update(float frameTime) {
    for (Component component : components.values()) {
      component.update(frameTime);
    }

    for (GameObject child : children) {
      child.update(frameTime);
    }
  }

  public void draw() {
    for (Component c : components.values()) {
      c.draw();
    }
    for (GameObject child : children) {
      child.draw();
    }
  }

  public boolean is(String tag) {
    return tags.contains(tag);
  }

  public void registerGetter(String name, Supplier<?> method) {
    getterMethods.put(name, method);
  }
  public void addMethod(String name, Supplier<?> getter, Consumer<?> setter) {
    getterMethods.put(name, getter);
    setterMethods.put(name, setter);
  }

  /** returns registered component property */
  public <E> E get(String property) {
    Supplier<?> getterMethid = getterMethods.get(property);
    return getterMethid == null ? null : (E) getterMethid.get();
  }

  /** sets registered component property */
  public <E> void set(String property, E value) {
    Consumer<E> setterMethod = (Consumer<E>) setterMethods.get(property);
    if (setterMethod != null) {
      setterMethod.accept(value);
    }
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

  public boolean isPaused() {
    return paused;
  }

  public void trigger(String eventName, Object ... params) {
    eventManager.trigger(eventName, Arrays.asList(params));
  }
}
