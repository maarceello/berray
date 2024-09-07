package com.berray;

import com.berray.components.core.AnchorType;
import com.berray.components.core.Component;
import com.berray.event.EventListener;
import com.berray.event.EventManager;
import com.berray.math.Matrix4;
import com.berray.math.Rect;
import com.berray.math.Vec2;
import com.berray.math.Vec3;


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
  protected Matrix4 worldTransformWithoutAnchor;
  /**
   * true when the local transformation is changed and therefore the world transformation
   * must be recalculated.
   */
  protected boolean transformDirty = true;

  /**
   * Bounding box in world coordinates.
   */
  protected Rect boundingBox;

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

  public Game getGame() {
    return game;
  }

  /** Returns the bounding box of the object in world coordinates. */
  public Rect getBoundingBox() {
    ensureTransformCalculated();
    return boundingBox;
  }

  /**
   * Add a GameObject as a child to this gameObject.
   * `components` is an array of:
   * <p>
   * - a {@link Component}. Components are added to the child gameobject as is.
   * - a {@link String}. I this case the string will be added as a tag to the child gameObject.
   * uses as the new object and all components and tags are added to this existing game object.
   */
  public GameObject add(Object... components) {
    return add(new GameObject(game, this), components);
  }

  /**
   * Add a GameObject as a child to this gameObject.
   * `components` is an array of:
   * <p>
   * - a {@link Component}. Components are added to the child gameobject as is.
   * - a {@link String}. I this case the string will be added as a tag to the child gameObject.
   * - the first component may be an instance of {@link GameObject}. In this case this object is
   * uses as the new object and all components and tags are added to this existing game object.
   */
  public <E extends GameObject> E add(E gameObject, Object... components) {
    if (components == null) {
      throw new NullPointerException("components must nor be null");
    }
    if (gameObject == null) {
      throw new NullPointerException("gameObject must nor be null");
    }
    gameObject.setGame(game);
    gameObject.addComponents(Arrays.asList(components));
    // trigger "add" event for all other interested parties
    addChild(gameObject);
    return gameObject;
  }

  public void remove(GameObject gameObject) {
    children.remove(gameObject);
    gameObject.parent = null;
    gameObject.game = null;
  }

  public void destroy() {
    for (Component component : components.values()) {
      component.destroy();
    }
    components.clear();
    // remove all event listeners
    if (game != null) {
      game.removeListener(this);
    }
  }


  public void setGame(Game game) {
    this.game = game;
    // tell each childs the game instance
    children.forEach(child -> child.setGame(game));
  }

  public Set<String> getTags() {
    return tags;
  }

  /** replaces the child at the specified index. Note that the removed child is not {@link #destroy() destoyed}. */
  public GameObject replaceChild(int index, GameObject other) {
    GameObject previous = children.set(index, other);
    previous.parent = null;
    previous.game = null;
    other.parent = this;
    other.setGame(this.game);
    // force recalculation of bounding rectangle
    transformDirty = true;
    trigger("add", this, other);
    other.trigger("add", this, other);
    return other ;
  }

  public GameObject addChild(GameObject other) {
    children.add(other);
    other.parent = this;
    other.setGame(this.game);
    // force recalculation of bounding rectangle
    transformDirty = true;
    trigger("add", this, other);
    other.trigger("add", this, other);
    return other ;
  }


  public void update(float frameTime) {
    // skip paused objects (and all of their children
    if (this.paused) {
      return;
    }
    // first update all children, then the object itself (depth first traversal)
    this.children.forEach(child -> child.update(frameTime));
    this.trigger("update", frameTime);
    // trigger game also, so tag bases listeners get notified
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
      } else if (c instanceof Property) {
        Property<?> property = (Property<?>) c;
        setProperty(property.getName(), property.getValue());
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

  public void draw() {
    if (paused) {
      return;
    }
    ensureTransformCalculated();
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

  public <E> void registerPropertyGetter(String name, Supplier<E> getter) {
    getterMethods.put(name, getter);
  }

  public <E> void registerPropertySetter(String name, Consumer<E> setter) {
    setterMethods.put(name, setter);
  }

  public <E> void registerProperty(String name, Supplier<E> getter, Consumer<E> setter) {
    getterMethods.put(name, getter);
    setterMethods.put(name, setter);
  }

  public void removeProperty(String name) {
    getterMethods.remove(name);
    setterMethods.remove(name);
  }

  /** Registers an action method with parameters and no return value. */
  public void registerAction(String name, Consumer<List<Object>> actionMethod) {
    actionMethods.put(name, params -> {
      actionMethod.accept(params);
      return null;
    });
  }

  /** Registers an action method without parameters and no return value. */
  public void registerAction(String name, Runnable actionMethod) {
    actionMethods.put(name, params -> {
      actionMethod.run();
      return null;
    });
  }


  /** Registers an action method with parameters and which returns a value. */
  public void registerAction(String name, Function<List<Object>, ?> actionMethod) {
    actionMethods.put(name, actionMethod);
  }

  public void removeAction(String name) {
    actionMethods.remove(name);
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
  public <E> E get(String property, E defaultValue) {
    return getOrDefault(property, defaultValue);
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
      throw new IllegalStateException("cannot find setter for property " + property + " in gameobject with tags " + tags);
    }
    setterMethod.accept(value);
  }

  public Set<String> getProperties() {
    Set<String> componentProperties = new HashSet<>();
    componentProperties.addAll(getterMethods.keySet());
    componentProperties.addAll(setterMethods.keySet());
    return componentProperties;
  }

  /**
   * calls a registered action, returning the result
   */
  public <E> E doAction(String methodName, Object... value) {
    Function<List<Object>, E> actionMethod = (Function<List<Object>, E>) actionMethods.get(methodName);
    if (actionMethod == null) {
      throw new IllegalStateException("can't find action method " + methodName + " on game object with tags " + tags);
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
   * TODO: this method is not well named. Rethink naming
   * Note: in kaboom this method is named `get(Tag)`, but we already have a method named `get`. Maybe rename the
   * existing {@link #get(String)} method?
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
    on(event, listener, null);
  }

  /**
   * add event listener.
   */
  public void on(String event, EventListener listener, Object owner) {
    eventManager.addEventListener(event, listener, owner);
  }

  /**
   * Removes all listeners belonging to the owner.
   */
  public void removeListener(Object owner) {
    eventManager.removeListener(owner);
  }

  public void setPaused(boolean paused) {
    this.paused = paused;
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

  public void onClick(EventListener eventListener) {
    this.trigger("click");
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
    // inform all children that the transform is invalid
    for (GameObject child : children) {
      if (!child.transformDirty) {
        child.setTransformDirty();
      }
    }
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
    ensureTransformCalculated();
    return worldTransform;
  }

  public Matrix4 getWorldTransformWithoutAnchor() {
    ensureTransformCalculated();
    return worldTransformWithoutAnchor;
  }

  private void ensureTransformCalculated() {
    if (transformDirty || (parent != null && parent.isTransformDirty())) {
      setTransformDirty(); // be sure to notify children that the transform is recalculated
      Vec2 pos = getOrDefault("pos", Vec2.origin());
      float angle = getOrDefault("angle", 0f);
      Vec2 size = getOrDefault("size", Vec2.origin());
      AnchorType anchor = getOrDefault("anchor", AnchorType.CENTER);
      float scale = getOrDefault("scale", 1.0f);

      float w2 = size.getX() / 2.0f;
      float h2 = size.getY() / 2.0f;

      float anchorX = w2 + anchor.getX() * w2;
      float anchorY = h2 + anchor.getY() * h2;

      localTransformWithoutAnchor = Matrix4.identity()
          .multiply(Matrix4.fromTranslate(pos.getX(), pos.getY(), 0))
          .multiply(Matrix4.fromRotatez((float) Math.toRadians(angle)))
          .multiply(Matrix4.fromScale(scale, scale, 1.0f));
      localTransform = localTransformWithoutAnchor
          .multiply(Matrix4.fromTranslate(-anchorX, -anchorY, 0));

      Matrix4 parentsWorldTransformWithoutAnchor = parent == null ? Matrix4.identity() : parent.getWorldTransformWithoutAnchor();
      Matrix4 parentsWorldTransform = parent == null ? Matrix4.identity() : parent.getWorldTransform();
      this.worldTransformWithoutAnchor = parentsWorldTransform.multiply(localTransformWithoutAnchor);

      worldTransform = parentsWorldTransform.multiply(localTransform);
      transformDirty = false;

      if (is("area")) {
        this.boundingBox = calculateBoundingBox(worldTransformWithoutAnchor, size, anchor);
      } else {
        this.boundingBox = null;
      }
    }
  }


  protected Rect calculateBoundingBox(Matrix4 worldTransformWithoutAnchor, Vec2 size, AnchorType anchor) {
    if (size.getX() < 0 || size.getY() < 0) {
      // a game object without dimensions cannot collide with anything
      return null;
    }

    float x = 0;
    float y = 0;
    float width = size.getX();
    float height = size.getY();

    // does this game object have any special collision area?
    Rect localArea = get("localArea");
    if (localArea != null) {
      // yes. set these coordinates to the collision rectangle
      x = localArea.getX();
      y = localArea.getY();
      width = localArea.getWidth();
      height = localArea.getHeight();
    }


    Vec2 anchorPoint = anchor.getAnchorPoint(new Vec2(width, height));
    float anchorX = anchorPoint.getX();
    float anchorY = anchorPoint.getY();


    Vec3 p1 = worldTransformWithoutAnchor.multiply(x + anchorX, y + anchorY, 0);
    Vec3 p2 = worldTransformWithoutAnchor.multiply(x + anchorX + width, y + anchorY, 0);
    Vec3 p3 = worldTransformWithoutAnchor.multiply(x + anchorX, y + anchorY + height, 0);
    Vec3 p4 = worldTransformWithoutAnchor.multiply(x + anchorX + width, y + anchorY + height, 0);

    float x1 = Math.min(p1.getX(), Math.min(p2.getX(), Math.min(p3.getX(), p4.getX())));
    float x2 = Math.max(p1.getX(), Math.max(p2.getX(), Math.max(p3.getX(), p4.getX())));

    float y1 = Math.min(p1.getY(), Math.min(p2.getY(), Math.min(p3.getY(), p4.getY())));
    float y2 = Math.max(p1.getY(), Math.max(p2.getY(), Math.max(p3.getY(), p4.getY())));

    // find combined bounding box of this game object and all of its children
    for (GameObject child : children) {
      Rect boundingBox = child.getBoundingBox();
      if (boundingBox != null) {
        x1 = Math.min(x1, boundingBox.getX());
        y1 = Math.min(y1, boundingBox.getY());
        x2 = Math.max(x2, boundingBox.getX() + boundingBox.getWidth());
        y2 = Math.max(y2, boundingBox.getY() + boundingBox.getHeight());
      }
    }


    return new Rect(x1, y1, x2 - x1, y2 - y1);
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

      // current child iterator has more elements?
      if (!childIterators.isEmpty() && childIterators.get(depthPosition).hasNext()) {
        return true;
      }

      // still more child iterators?
      if ((depthPosition + 1) < childIterators.size()) {
        return true;
      }

      // our own childs are processed and all child iterators. so no more elements available.
      return false;
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

        depthPosition += 1;

        // current iterator is drained, but we have another child iterator ?
        if (depthPosition < children.size()) {
          // increase depthPosition and return the element from this iterator.
          return childIterators.get(depthPosition).next();
        }
      }

      // no element in out own list, current iterator is drained and
      // we don't have more iterators. so we don't have an element to return
      throw new NoSuchElementException();
    }
  }


  public static GameObject make(Object... components) {
    GameObject object = new GameObject();
    object.addComponents(components);
    return object;
  }

  public static <E extends GameObject> E make(E gameObject, Object... components) {
    if (gameObject == null) {
      throw new IllegalArgumentException("gameObject may not be null");
    }
    gameObject.addComponents(components);
    return gameObject;
  }
}
