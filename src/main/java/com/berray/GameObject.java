package com.berray;

import com.berray.components.core.AnchorType;
import com.berray.components.core.Component;
import com.berray.event.EventListener;
import com.berray.event.EventManager;
import com.berray.math.Matrix4;
import com.berray.math.Vec2;


import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
  /**
   * registered action methods.
   */
  private final Map<String, Function<List<Object>, ?>> actionMethods = new HashMap<>();


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
   * local transformation relative to the parent, without the applied anchor.
   */
  protected Matrix4 localTransformWithoutAnchor = Matrix4.identity();
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

  /**
   * Add a GameObject as a child to this gameObject.
   * `components` is an array of:
   *
   * - a {@link Component}. Components are added to the child gameobject as is.
   * - a {@link String}. I this case the string will be added as a tag to the child gameObject.
   * - the first component may be an instance of {@link GameObject}. In this case this object is
   * uses as the new object and all components and tags are added to this existing game object.
   */
  public GameObject add(Object... components) {
    if (components == null) {
      throw new NullPointerException("components is null");
    }
    GameObject gameObject;
    if (components.length > 0 && components[0] instanceof GameObject) {
      gameObject = (GameObject) components[0];
      gameObject.addComponents(Arrays.asList(components).subList(1, components.length));
    } else {
      gameObject = new GameObject(game, this);
      gameObject.addComponents(Arrays.asList(components));
    }
    // trigger add event for all other interested parties
    addChild(gameObject);
    return gameObject;
  }

  public void setGame(Game game) {
    this.game = game;
    // tell each childs the game instance
    children.forEach(child -> child.setGame(game));
  }

  public Set<String> getTags() {
    return tags;
  }

  public void addChild(GameObject other) {
    children.add(other);
    other.parent = this;
    other.setGame(this.game);
    trigger("add", this, other);
    other.trigger("add", this, other);
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

  public void addComponents(Object... components) {
    addComponents(Arrays.asList(components));
  }

  /**
   * add components to this game object and trigger "add" event.
   */
  public void addComponents(List<Object> components) {
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

  public <E> void registerMethod(String name, Supplier<E> getter, Consumer<E> setter) {
    getterMethods.put(name, getter);
    setterMethods.put(name, setter);
  }

  public void registerAction(String name, Consumer<List<Object>> actionMethod) {
    actionMethods.put(name, (params) -> {
      actionMethod.accept(params);
      return null;
    });
  }

  public void registerAction(String name, Function<List<Object>, ?> actionMethod) {
    actionMethods.put(name, actionMethod);
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
    if (setterMethod == null) {
      throw new IllegalStateException("cannot find setter for property "+property+" in gameobject with tags "+tags);
    }
    setterMethod.accept(value);
  }

  /**
   * calls a registered action, returning the result
   */
  public <E> E doAction(String methodName, Object... value) {
    Function<List<Object>, E> actionMethod = (Function<List<Object>, E>) actionMethods.get(methodName);
    if (actionMethod == null) {
      throw new IllegalStateException("can't find action method " + methodName+" on game object with tags "+tags);
    }
    return actionMethod.apply(value == null ? Collections.emptyList() : Arrays.asList(value));
  }

  /**
   * Returns an iterator over all children game objects.
   *
   * @param recursive when true the children are returned recursivly (breadth-first)
   */
  public Iterator<GameObject> childrenIterator(boolean recursive) {
    return new ChildIterator(children, recursive);
  }

  /**
   * Returns all game objects recursively as a stream. The iteration order is breath-first.
   */
  public Stream<GameObject> getGameObjectStream() {
    return StreamSupport.stream(
        Spliterators.spliteratorUnknownSize
            (childrenIterator(true), Spliterator.ORDERED), false);
  }

  /**
   * Returns all game objects with the specified tag.
   */
  public Stream<GameObject> getTagStream(String tag) {
    return getGameObjectStream()
        .filter(object -> object.is(tag));
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

  public GameObject getRoot() {
    return (parent == null) ? this : parent.getRoot();
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

  public Matrix4 getLocalTransform() {
    return localTransform;
  }

  public Matrix4 getLocalTransformWithoutAnchor() {
    return localTransformWithoutAnchor;
  }

  public Matrix4 getWorldTransform() {
    if (transformDirty) {
      Vec2 pos = getOrDefault("pos", Vec2.origin());
      float angle = getOrDefault("angle", 0f);
      Vec2 size = getOrDefault("size", Vec2.origin());
      AnchorType anchor = getOrDefault("anchor", AnchorType.CENTER);

      float w2 = size.getX() / 2.0f;
      float h2 = size.getY() / 2.0f;

      float anchorX = w2 + anchor.getX() * w2;
      float anchorY = h2 + anchor.getY() * h2;

      localTransformWithoutAnchor = Matrix4.fromTranslate(pos.getX(), pos.getY(), 0)
          .multiply(Matrix4.fromRotatez((float) Math.toRadians(angle)));
      localTransform = localTransformWithoutAnchor.multiply(Matrix4.fromTranslate(-anchorX, -anchorY, 0));
      worldTransform = parent == null ? localTransform : parent.getWorldTransform().multiply(localTransform);
      transformDirty = false;
    }
    return worldTransform;
  }

  public boolean exists() {
    return parent != null;
  }

  private static class ChildIterator implements Iterator<GameObject> {
    private final List<GameObject> children;
    private final List<ChildIterator> childIterators;

    private int position = 0;
    private int depthPosition = 0;

    public ChildIterator(List<GameObject> children, boolean recursive) {
      this.children = new ArrayList<>(children);
      this.childIterators = new ArrayList<>();
      if (recursive) {
        for (GameObject child : children) {
          List<GameObject> children1 = child.getChildren();
          if (!children1.isEmpty()) {
            childIterators.add(new ChildIterator(children1, true));
          }
        }
      }
    }

    @Override
    public boolean hasNext() {
      // still one element in our own children?
      if (position < children.size()) {
        return true;
      }

      // no more child iterators?
      if (depthPosition >= childIterators.size()) {
        // => no more elements
        return false;
      }

      // either the current child iterator has one more element or
      // we have another child iterator (which we checked just before)
      return true;
    }

    @Override
    public GameObject next() {
      // still an element in our own list? return it (and increase position).
      if (position < children.size()) {
        return children.get(position++);
      }

      if (!childIterators.isEmpty()) {

        // the current iterator still has elements? return it.
        if (childIterators.get(depthPosition).hasNext()) {
          return childIterators.get(depthPosition).next();
        }

        // current iterator is drained, but we have another child iterator ?
        if (depthPosition < children.size()) {
          // increase depthPosition and return the element from this iterator.
          return childIterators.get(depthPosition++).next();
        }
      }

      // no element in out own list, current iterator is drained and
      // we don't have more iterators. so we don't have an element to return
      throw new NoSuchElementException();
    }
  }
}
