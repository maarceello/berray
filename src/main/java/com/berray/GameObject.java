package com.berray;

import com.berray.components.core.AnchorType;
import com.berray.components.core.Component;
import com.berray.event.EventListener;
import com.berray.event.*;
import com.berray.math.Matrix4;
import com.berray.math.Rect;
import com.berray.math.Vec2;
import com.berray.math.Vec3;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.berray.event.CoreEvents.PHYSICS_COLLIDE;
import static com.berray.event.CoreEvents.SCENE_GRAPH_ADDED;
import static com.raylib.Raylib.*;

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
  protected final Map<Class<?>, Component> components;

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
  private Matrix4 localTransformWithoutAnchor = Matrix4.identity();
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
  /**
   * Order in which to draw the game objects children.
   */
  protected DrawOrder drawOrder = DrawOrder.DEPTH_FIRST;

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

  public void setGame(Game game) {
    this.game = game;
    // tell each childs the game instance
    children.forEach(child -> child.setGame(game));
    if (game != null) {
      // fire event that the object was added to the scene graoh
      trigger(SCENE_GRAPH_ADDED, this);
    }
  }

  /**
   * Returns the bounding box of the object in world coordinates.
   */
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
    // trigger event that the object was removed from the scene graph
    gameObject.emitSceneGraphRemovedEvent(this);
  }

  /**
   * Fired when the game object or ist subtree was removed from the scene graph
   *
   * @param removePoint game object from which the subtree was removed
   * @type emit-event
   */
  protected void emitSceneGraphRemovedEvent(GameObject removePoint) {
    trigger(CoreEvents.SCENE_GRAPH_REMOVED, removePoint);
    // also notify children that the subtree was removed
    for (GameObject child : children) {
      child.emitSceneGraphRemovedEvent(removePoint);
    }
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


  public Set<String> getTags() {
    return tags;
  }

  /**
   * replaces the child at the specified index. Note that the removed child is not {@link #destroy() destoyed}.
   */
  public GameObject replaceChild(int index, GameObject other) {
    GameObject previous = children.set(index, other);
    previous.parent = null;
    previous.game = null;
    previous.emitSceneGraphRemovedEvent(this);

    other.parent = this;
    other.setGame(this.game);
    // force recalculation of bounding rectangle
    transformDirty = true;
    trigger(CoreEvents.ADD, this, other);
    other.trigger(CoreEvents.ADD, this, other);
    return other;
  }

  public GameObject addChild(GameObject child) {
    children.add(child);
    child.parent = this;
    child.setGame(this.game);
    // force recalculation of bounding rectangle
    transformDirty = true;
    trigger(CoreEvents.ADD, this, child);
    child.trigger(CoreEvents.ADD, this, child);
    return child;
  }


  public void update(float frameTime) {
    // skip paused objects (and all of their children
    if (this.paused) {
      return;
    }
    // first update all children, then the object itself (depth first traversal)
    this.children.forEach(child -> child.update(frameTime));
    this.trigger(CoreEvents.UPDATE, this, frameTime);
    // trigger game also, so tag based listeners get notified
    this.game.trigger(CoreEvents.UPDATE, this, frameTime);
  }

  public List<GameObject> getChildren() {
    return children;
  }

  /**
   * Returns children with the specified tag. This method checks only the direct children of the game object.
   *
   * @see #getTagStream(String)
   */
  public List<GameObject> getChildren(String tag) {
    return children.stream().filter(child -> child.is(tag)).collect(Collectors.toList());
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
        if (this.components.containsKey(component.getClass())) {
          throw new IllegalArgumentException("Component " + component.getClass() + " is already registered in object with tags " + tags);
        }
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

  /**
   * Called by the game to get the code, which will be called to render the object.
   */
  public void visitDraw(BiConsumer<String, Runnable> visitor) {
    if (paused) {
      return;
    }

    if (Boolean.TRUE.equals(get("render", false))) {
      visitor.accept(get("layer", Game.DEFAULT_LAYER), () -> {
        rlPushMatrix();
        {
          ensureTransformCalculated();
          rlMultMatrixf(getWorldTransform().toFloatTransposed());
          for (Component c : components.values()) {
            c.draw();
          }
        }
        rlPopMatrix();
      });
    }
  }

  /**
   * Called by the game to get the code, which will be called to render the objects children.
   */
  public void visitDrawChildren(BiConsumer<String, Runnable> visitor) {
    // don't draw children of paused objects
    if (paused) {
      return;
    }

    if (drawOrder == DrawOrder.DEPTH_FIRST) {
      // depth first: for each child visit the object, followed by its children.
      for (GameObject child : getChildren()) {
        child.visitDraw(visitor);
        child.visitDrawChildren(visitor);
      }
    } else {
      // breath first: for each child draw the object
      for (GameObject child : getChildren()) {
        child.visitDraw(visitor);
      }
      // then all children are drawn, for each child draw their children
      for (GameObject child : getChildren()) {
        child.visitDrawChildren(visitor);
      }
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

  /**
   * Registers a property setter which triggers a <code>propertyChange</code> event when the property is changed.
   * The property name is remembered. Upon deletion the properties will be removed.
   * <p>
   * Note: to compare the old and the new value the getter is needed too.
   */
  public <E> void registerBoundPropertySetter(String name, Supplier<E> getter, Consumer<E> setter) {
    registerPropertySetter(name, (E newValue) ->
        setter.accept(firePropertyChange(name, getter.get(), newValue))
    );
  }

  public <E> void registerProperty(String name, Supplier<E> getter, Consumer<E> setter) {
    if (getterMethods.containsKey(name)) {
      throw new IllegalStateException("property getter " + name + " already registered with " + getterMethods.get(name));
    }
    getterMethods.put(name, getter);
    if (setterMethods.containsKey(name)) {
      throw new IllegalStateException("property setter " + name + " already registered with " + setterMethods.get(name));
    }
    setterMethods.put(name, setter);
  }

  /**
   * Registers a property which triggers a <code>propertyChange</code> event when the property is changed.
   * The property name is remembered. Upon deletion the properties will be removed.
   */
  public <E> void registerBoundProperty(String name, Supplier<E> getter, Consumer<E> setter) {
    registerProperty(name, getter, newValue ->
        setter.accept(firePropertyChange(name, getter.get(), newValue))
    );
  }

  public void removeProperty(String name) {
    getterMethods.remove(name);
    setterMethods.remove(name);
  }

  /**
   * Checks if the new property is different from the old value and fires an <code>propertyChange</code> event if they are.
   * For convenience the new property is returned.
   */
  public <E> E firePropertyChange(String propertyName, E oldValue, E newValue) {
    if (!Objects.equals(oldValue, newValue)) {
      trigger(CoreEvents.PROPERTY_CHANGED, this, propertyName, oldValue, newValue);
    }

    return newValue;
  }

  /**
   * Registers an action method with parameters and no return value.
   */
  public void registerAction(String name, Consumer<List<Object>> actionMethod) {
    actionMethods.put(name, params -> {
      actionMethod.accept(params);
      return null;
    });
  }

  /**
   * Registers an action method without parameters and no return value.
   */
  public void registerAction(String name, Runnable actionMethod) {
    actionMethods.put(name, params -> {
      actionMethod.run();
      return null;
    });
  }


  /**
   * Registers an action method with parameters and which returns a value.
   */
  public void registerAction(String name, Function<List<Object>, ?> actionMethod) {
    actionMethods.put(name, actionMethod);
  }

  public void removeAction(String name) {
    actionMethods.remove(name);
  }

  /**
   * returns registered component property
   */
  @SuppressWarnings("unchecked")
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

  public boolean canWrite(String property) {
    return setterMethods.containsKey(property);
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
  @SuppressWarnings("unchecked")
  public <E> void set(String property, E value) {
    Consumer<E> setterMethod = (Consumer<E>) setterMethods.get(property);
    if (setterMethod == null) {
      throw new IllegalStateException("cannot find setter for property " + property + " in gameobject " + getClass().getSimpleName() + " with tags " + tags);
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
  @SuppressWarnings("unchecked")
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

  @SuppressWarnings("unchecked")
  public <E> E getProperty(String property) {
    return (E) properties.get(property);
  }

  @SuppressWarnings("unchecked")
  public <E extends Component> E getComponent(Class<E> type) {
    return (E) components.get(type);
  }

  public void addTag(String tag) {
    tags.add(tag);
  }

  /**
   * add event listener.
   */
  public <E extends Event> void on(String event, EventListener<E> listener) {
    on(event, listener, null);
  }

  /**
   * add event listener.
   */
  public <E extends Event> void on(String event, EventListener<E> listener, Object owner) {
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

  public void trigger(Event event) {
    eventManager.trigger(event);
  }

  public GameObject getRoot() {
    return (parent == null) ? this : parent.getRoot();
  }

  /**
   * Update all game objects
   */
  public <E extends PhysicsCollideEvent> void onCollide(String tag, EventListener<E> eventListener) {
    on(PHYSICS_COLLIDE, (E event) -> {
      GameObject gameObject = event.getCollisionPartner();
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

  protected void ensureTransformCalculated() {
    if (transformDirty || (parent != null && parent.isTransformDirty())) {
      setTransformDirty(); // be sure to notify children that the transform is recalculated
      Matrix4 posMatrix = getOrDefault("posTransform", Matrix4.identity());
      Matrix4 rotationMatrix = getOrDefault("rotationMatrix", Matrix4.identity());
      Vec2 size = getOrDefault("size", Vec2.origin());
      AnchorType anchor = getOrDefault("anchor", AnchorType.TOP_LEFT);
      Vec3 scale = getOrDefault("scale", new Vec3(1.0f, 1.0f, 1.0f));

      float w2 = size.getX() / 2.0f;
      float h2 = size.getY() / 2.0f;

      float anchorX = w2 + anchor.getX() * w2;
      float anchorY = h2 + anchor.getY() * h2;

      localTransformWithoutAnchor = Matrix4.identity()
          .multiply(posMatrix)
          .multiply(rotationMatrix)
          .multiply(Matrix4.fromScale(scale.getX(), scale.getY(), scale.getZ()));
      localTransform = localTransformWithoutAnchor
          .multiply(Matrix4.fromTranslate(-anchorX, -anchorY, 0));

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
    // when the game object supplies its own bounding box, there is no need for a `size` property
    Rect customBoundingBox = get("boundingBox");
    if (customBoundingBox != null) {
      return customBoundingBox;
    }

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
      Rect childBoundingBox = child.getBoundingBox();
      if (childBoundingBox != null) {
        x1 = Math.min(x1, childBoundingBox.getX());
        y1 = Math.min(y1, childBoundingBox.getY());
        x2 = Math.max(x2, childBoundingBox.getX() + childBoundingBox.getWidth());
        y2 = Math.max(y2, childBoundingBox.getY() + childBoundingBox.getHeight());
      }
    }


    return new Rect(x1, y1, x2 - x1, y2 - y1);
  }

  public Vec3 worldPosToLocalPos(Vec3 worldPos) {
    Matrix4 inverseTransform = getWorldTransform().inverse();
    return inverseTransform.multiply(worldPos);
  }

  public Vec2 worldPosToLocalPos(Vec2 worldPos) {
    Matrix4 inverseTransform = getWorldTransform().inverse();
    return inverseTransform.multiply(worldPos.getX(), worldPos.getY(), 0).toVec2();
  }


  public boolean exists() {
    return parent != null;
  }

  protected boolean containsComponent(List<Object> components, String tag) {
    return components.stream()
        .filter(Component.class::isInstance)
        .anyMatch(c -> ((Component) c).getTag().equals(tag));
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


  public static GameObject makeGameObject(Object... components) {
    GameObject object = new GameObject();
    object.addComponents(components);
    return object;
  }

  public static <E extends GameObject> E makeGameObject(E gameObject, Object... components) {
    if (gameObject == null) {
      throw new IllegalArgumentException("gameObject may not be null");
    }
    gameObject.addComponents(components);
    return gameObject;
  }
}
