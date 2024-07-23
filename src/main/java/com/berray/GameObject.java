package com.berray;

import com.berray.components.Component;
import com.berray.event.EventListener;
import com.berray.event.EventManager;
import com.berray.math.Matrix4;
import com.berray.math.Vec2;


import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GameObject {
  private static final AtomicInteger nextComponentId = new AtomicInteger(0);
  private static final AtomicInteger nextGameObjectId = new AtomicInteger(0);
  private int id;
  /**
   * Tags.
   */
  private Set<String> tags = new HashSet<>();
  /**
   * Components for this game object.
   */
  private final Map<Class<?>, Component> components;

  /**
   * registered getter methods from components.
   */
  private final Map<String, Supplier<?>> getterMethods = new HashMap<>();
  /**
   * registered setter methods from components.
   */
  private final Map<String, Consumer<?>> setterMethods = new HashMap<>();
  private final Map<String, Object> properties = new HashMap<>();
  /**
   * event manager for game object local event.
   */
  private final EventManager eventManager = new EventManager();
  private boolean paused = false;

  /**
   * child game objects.
   */
  private List<GameObject> children = new LinkedList<>();
  private GameObject parent;
  protected Game game;
  /**
   * local transformation relative to the parent.
   */
  protected Matrix4 localTransform = Matrix4.identity();
  /**
   * transformation relative to the world.
   */
  protected Matrix4 worldTransform;
  /**
   * true when the local transformation is changed and therefore the world transformation
   * must be recalculated.
   */
  protected boolean transformDirty = true;

  public GameObject() {
    this.components = new LinkedHashMap<>();
    this.id = nextGameObjectId.incrementAndGet();
  }

  public GameObject(Game game) {
    this();
    this.game = game;
  }

  public GameObject(Game game, GameObject parent) {
    this(game);
    this.parent = parent;
  }

  public int getId() {
    return id;
  }

  public GameObject add(Object... components) {
    GameObject gameObject = new GameObject(game, this);
    gameObject.addComponents(components);
    // trigger add event for all other interested parties
    addChild(gameObject);
    return gameObject;
  }

  public void addChild(GameObject other) {
    children.add(other);
    other.parent = this;
    other.game = this.game;
    trigger("add", this, other);
  }


  public void update(float frameTime) {
    // skip paused objects (and all of their children
    if (this.paused) {
      return;
    }
    // first update all children, then the object itself (depth first traversal)
    this.children.forEach((child) -> child.update(frameTime));
    this.trigger("update", frameTime);
    // trigger game also, to tag bases listeners get notified
    this.game.trigger("update", this, frameTime);
  }

  public List<GameObject> getChildren() {
    return children;
  }

  /**
   * only for classes extending GameObject: add components to this game object and trigger "add" event.
   */
  protected void addComponents(Object... components) {
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
        throw new IllegalArgumentException("Component of type " + c.getClass() + " not supported. Either add a tag (String) or a component (Component)");
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

  public void registerSetter(String name, Consumer<?> setter) {
    setterMethods.put(name, setter);
  }

  public void addMethod(String name, Supplier<?> getter, Consumer<?> setter) {
    getterMethods.put(name, getter);
    setterMethods.put(name, setter);
  }

  /**
   * returns registered component property
   */
  public <E> E get(String property) {
    Supplier<?> getterMethod = getterMethods.get(property);
    return getterMethod == null ? null : (E) getterMethod.get();
  }

  /**
   * returns registered component property
   */
  public <E> E getOrDefault(String property, E defaultValue) {
    E value = get(property);
    return value == null ? defaultValue : value;
  }

  /**
   * sets registered component property
   */
  public <E> void set(String property, E value) {
    Consumer<E> setterMethod = (Consumer<E>) setterMethods.get(property);
    if (setterMethod != null) {
      setterMethod.accept(value);
    }
  }

  public void setProperty(String property, Object value) {
    properties.put(property, value);
  }

  public <E> E getProperty(String property) {
    return (E) properties.get(property);
  }

  public <E extends Component> E getComponent(Class<E> type) {
    return (E) components.get(type);
  }

  public void addTag(String tag) {
    tags.add(tag);
  }

  /**
   * add event listener.
   */
  public void on(String event, EventListener listener) {
    eventManager.addEventListener(event, listener);
  }

  public boolean isPaused() {
    return paused;
  }

  public void trigger(String eventName, Object... params) {
    eventManager.trigger(eventName, Arrays.asList(params));
  }

  /**
   * Update all game objects
   */
  public void onCollide(String tag, EventListener eventListener) {
    on("collide", event -> {
      GameObject gameObject = event.getParameter(0);
      // only propagate event when the object has the required tag
      if (gameObject.is(tag)) {
        eventListener.onEvent(event);
      }
    });
  }

  /**
   * marks the objects that the world transformation should be recalculated .
   */
  public void setTransformDirty() {
    transformDirty = true;
  }

  /**
   * Returns true when this object or a parent is dirty.
   */
  public boolean isTransformDirty() {
    // do we have a parent and are *not* dirty?
    if (parent != null && !transformDirty) {
      // yes. if the parent is dirty, set us dirty too.
      boolean parentDirty = parent.isTransformDirty();
      if (parentDirty) {
        this.transformDirty = true;
      }
    }
    // return dirty flag which may be copied from parent
    return transformDirty;
  }

  public Matrix4 getWorldTransform() {
    if (transformDirty) {
      Vec2 pos = getOrDefault("pos", Vec2.origin());
      localTransform = Matrix4.fromTranslate(pos.getX(), pos.getY(), 0);
      worldTransform = parent == null ? localTransform : localTransform.multiply(parent.getWorldTransform());
      transformDirty = false;
    }
    return worldTransform;
  }

}
