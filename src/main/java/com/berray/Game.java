package com.berray;

import com.berray.assets.loader.AssetLoaders;
import com.berray.assets.DefaultAssetManager;
import com.berray.assets.loader.RaylibAssetLoader;
import com.berray.event.*;
import com.berray.event.EventListener;
import com.berray.math.Collision;
import com.berray.math.Rect;
import com.berray.math.Vec2;
import com.berray.math.Vec3;
import com.berray.objects.gui.DefaultLookAndFeel;
import com.berray.objects.gui.LookAndFeelManager;
import com.raylib.Raylib;

import java.nio.file.FileSystems;
import java.util.*;
import java.util.stream.Collectors;

import static com.berray.event.CoreEvents.PHYSICS_COLLIDE_UPDATE;

public class Game {
  public static final String DEFAULT_LAYER = "default";
  private final AssetLoaders assetLoaders;
  /**
   * Gravity vector.
   */
  private Vec3 gravity;
  /**
   * normalized gravity vector.
   */
  private Vec3 gravityDirection;
  /** current root object */
  private GameObject root;
  /** root object for 2d rendering */
  private List<String> layers = new ArrayList<>();

  private EventManager eventManager;
  private DefaultAssetManager assetManager;
  private MouseManager mouseManager;
  private LookAndFeelManager lookAndFeelManager = new DefaultLookAndFeel();


  // Constructor
  public Game() {
    root = new GameObject(this).add("root");
    assetLoaders = new AssetLoaders();
    assetLoaders.addAssetLoader(new RaylibAssetLoader());
    assetManager = new DefaultAssetManager(assetLoaders, FileSystems.getDefault().getPath("."));
    EventTypeFactory eventTypeFactory = EventTypeFactory.getInstance();
    eventTypeFactory.registerEventType(PropertyChangeEvent.EVENT_NAME, PropertyChangeEvent::new);
    eventTypeFactory.registerEventType(UpdateEvent.EVENT_NAME, UpdateEvent::new);
    eventTypeFactory.registerEventType(AddEvent.EVENT_NAME, AddEvent::new);
    eventTypeFactory.registerEventType(KeyEvent.EVENT_NAME_KEY_PRESS, KeyEvent::new);
    eventTypeFactory.registerEventType(KeyEvent.EVENT_NAME_KEY_DOWN, KeyEvent::new);
    eventTypeFactory.registerEventType(KeyEvent.EVENT_NAME_KEY_UP, KeyEvent::new);
    eventTypeFactory.registerEventType(MouseEvent.EVENT_NAME_MOUSE_MOVE, MouseEvent::createMouseEvent);
    eventTypeFactory.registerEventType(MouseEvent.EVENT_NAME_MOUSE_WHEEL_MOVE, MouseEvent::createMouseEvent);
    eventTypeFactory.registerEventType(MouseEvent.EVENT_NAME_MOUSE_PRESS, MouseEvent::createMouseEvent);
    eventTypeFactory.registerEventType(MouseEvent.EVENT_NAME_MOUSE_RELEASE, MouseEvent::createMouseEvent);
    eventTypeFactory.registerEventType(MouseEvent.EVENT_NAME_MOUSE_CLICK, MouseEvent::createMouseEvent);
    eventTypeFactory.registerEventType(MouseEvent.EVENT_NAME_DRAG_START, MouseEvent::createMouseEvent);
    eventTypeFactory.registerEventType(MouseEvent.EVENT_NAME_DRAGGING, MouseEvent::createMouseEvent);
    eventTypeFactory.registerEventType(MouseEvent.EVENT_NAME_DRAG_FINISH, MouseEvent::createMouseEvent);
    eventTypeFactory.registerEventType(MouseEvent.EVENT_NAME_HOVER, MouseEvent::createMouseEvent);
    eventTypeFactory.registerEventType(MouseEvent.EVENT_NAME_HOVER_LEAVE, MouseEvent::createMouseEvent);
    eventTypeFactory.registerEventType(MouseEvent.EVENT_NAME_HOVER_ENTER, MouseEvent::createMouseEvent);
    eventTypeFactory.registerEventType(AnimationEvent.EVENT_NAME_ANIMATION_END, AnimationEvent::new);
    eventTypeFactory.registerEventType(AnimationEvent.EVENT_NAME_ANIMATION_START, AnimationEvent::new);
    eventTypeFactory.registerEventType(SceneGraphEvent.EVENT_NAME_ADDED, SceneGraphEvent::new);
    eventTypeFactory.registerEventType(SceneGraphEvent.EVENT_NAME_REMOVED, SceneGraphEvent::new);
    eventTypeFactory.registerEventType(PhysicsBeforeResolveEvent.EVENT_NAME, PhysicsBeforeResolveEvent::new);
    eventTypeFactory.registerEventType(PhysicsCollideEvent.EVENT_NAME, PhysicsCollideEvent::new);
    eventTypeFactory.registerEventType(PhysicsCollideUpdateEvent.EVENT_NAME, PhysicsCollideUpdateEvent::new);
    eventTypeFactory.registerEventType(PhysicsResolveEvent.EVENT_NAME, PhysicsResolveEvent::new);
    eventTypeFactory.registerEventType(PhysicsEvent.EVENT_NAME_GROUND,  PhysicsEvent::new);
    eventTypeFactory.registerEventType(PhysicsEvent.EVENT_NAME_HEADBUTT,  PhysicsEvent::new);
    eventTypeFactory.registerEventType(PhysicsEvent.EVENT_NAME_FALL,  PhysicsEvent::new);
    eventTypeFactory.registerEventType(PhysicsEvent.EVENT_NAME_FALL_OFF,  PhysicsEvent::new);
    eventTypeFactory.registerEventType(ActionEvent.EVENT_NAME, ActionEvent::new);
    eventManager = new EventManager();
    mouseManager = new MouseManager();
    init();
  }

  public MouseManager getMouseManager() {
    return mouseManager;
  }

  public DefaultAssetManager getAssetManager() {
    return assetManager;
  }

  public AssetLoaders getAssetLoaders() {
    return assetLoaders;
  }

  public LookAndFeelManager getDefaultLookAndFeelManager() {
    return lookAndFeelManager;
  }

  public void setDefaultLookAndFeelManager(LookAndFeelManager lookAndFeelManager) {
    this.lookAndFeelManager = lookAndFeelManager;
  }

  public void init() {
    // forward game objects adds to game event manager
    root.on(CoreEvents.ADD, event -> eventManager.trigger(event.getName(), event.getParameters()));
    // forward update events down the object tree
    on(CoreEvents.UPDATE, event -> root.trigger(event.getName(), event.getParameters()));
    layers.add(DEFAULT_LAYER);
    // forward mouse events to mouse mangager
    mouseManager.registerListeners(this);
  }

  public void setLayers(List<String> layers) {
    this.layers = layers;
  }

  public List<String> getLayers() {
    return layers;
  }

  public float width() {
    return Raylib.GetRenderWidth();
  }

  public float height() {
    return Raylib.GetRenderHeight();
  }

  public Vec2 center() {
    return new Vec2(width() / 2, height() / 2);
  }

  public void setGravity(int gravity) {
    this.gravity = new Vec3(0, gravity, 0);
    this.gravityDirection = this.gravity.normalize();
  }

  public Vec3 getGravityDirection() {
    return gravityDirection;
  }

  public Vec3 getGravity() {
    return gravity;
  }

  public GameObject getRoot() {
    return root;
  }

  public <E extends GameObject> E add(E gameObject, Object... components) {
    E newGameObject = root.add(gameObject, components);
    // forward add events from the new game object to game event manager
    newGameObject.on(CoreEvents.ADD, event -> eventManager.trigger(event.getName(), event.getParameters()));
    return newGameObject;
  }
  /**
   * Add a game object to the game
   */
  public GameObject add(Object... components) {
    GameObject newGameObject = root.add(components);
    // forward add events from the new game object to game event manager
    newGameObject.on(CoreEvents.ADD, event -> eventManager.trigger(event.getName(), event.getParameters()));
    return newGameObject;
  }

  /**
   * Add a game object to the game
   */
  public void addChild(GameObject child) {
    root.addChild(child);
  }


  /**
   * Update all game objects
   */
  public void update(float frameTime) {
    eventManager.trigger(CoreEvents.UPDATE, Arrays.asList(null, frameTime));
    root.update(frameTime);
  }

  /**
   * Draw all game objects
   */
  public void draw() {
    Map<String, List<Runnable>> sortedLayers = new LinkedHashMap<>();
    // insert empty list for each layer in the correct order
    layers.forEach(layerName -> sortedLayers.put(layerName, new ArrayList<>()));

    root.visitDrawChildren((String layer, Runnable drawMethod) -> {
      if (layer == null) {
        layer = DEFAULT_LAYER;
      }
      List<Runnable> layerList = sortedLayers.get(layer);
      if (layerList == null) {
        throw new IllegalStateException("layer " + layer + " not allowed in game object with draw method "+drawMethod);
      }
      layerList.add(drawMethod);
    });

    // call each render method, in the order of the layers
    sortedLayers.values()
        .stream()
        .flatMap(List::stream)
        .forEach(Runnable::run);
  }

  private Vec2 collides(Rect a, Rect b) {
    float rax1 = a.getX();
    float ray1 = a.getY();
    float rax2 = a.getX() + a.getWidth();
    float ray2 = a.getY() + a.getHeight();

    float rbx1 = b.getX();
    float rby1 = b.getY();
    float rbx2 = b.getX() + b.getWidth();
    float rby2 = b.getY() + b.getHeight();

    if (
        rax1 >= rbx2 || // a is to the right of b
            ray1 >= rby2 || // a is below b
            rax2 < rbx1 || // a is to the left of b
            ray2 < rby1 // a is above b
    ) {
      // not colliding
      return null;
    }

    // find the shortest vector in which to move the object a so the collision is resolved
    Vec2 displacement = Vec2.origin();
    float minDistance = Float.MAX_VALUE;
    // upper edge in collision? Move upper edge down
    if (ray1 > rby1 && ray1 < rby2) {
      float distance = rby2 - ray1;
      if (distance < minDistance) {
        minDistance = distance;
        displacement = new Vec2(0, distance);
      }
    }
    // lower edge in collision? Move lower edge up
    if (ray2 >= rby1 && ray2 < rby2) {
      float distance = ray2 - rby1;
      if (distance < minDistance) {
        minDistance = distance;
        displacement = new Vec2(0, -distance);
      }
    }
    // left edge in collision? Move left edge to the right
    if (rax1 > rbx1 && rax1 < rbx2) {
      float distance = rbx2 - rax1;
      if (distance < minDistance) {
        minDistance = distance;
        displacement = new Vec2(distance, 0);
      }
    }
    // right edge in collision? Move right edge to the left
    if (rax2 >= rbx1 && rax2 < rbx2) {
      float distance = rax2 - rbx1;
      if (distance < minDistance) {
        minDistance = distance;
        displacement = new Vec2(-distance,0 );
      }
    }

    return displacement;
  }

  public void updateCollisions() {
    // optimization and collision detection between arbitrarily oriented polygons
    // Reduce number of object to object collision detections
    // * some kind of bsp or quad tree
    // * https://en.wikipedia.org/wiki/Sweep_and_prune
    // Separating Axis Theorem:
    // * https://www.sevenson.com.au/programming/sat/
    // * https://code.tutsplus.com/collision-detection-using-the-separating-axis-theorem--gamedev-169t

    // linearize the object tree
    List<GameObject> gameObjects = new ArrayList<>();
    for (GameObject gameObject : root.getChildren()) {
      addGameObjects(gameObject, gameObjects);
    }
    // only keep game objects with area componentLIME
    List<GameObject> areaObjects = gameObjects.stream()
        // only game objects with area component may participate in collision detection
        .filter(gameObject -> gameObject.is("area"))
        // paused game objects don't participate in collision detection
        .filter(gameObject -> !gameObject.isPaused())
        .collect(Collectors.toList());

    for (int i = 0; i < areaObjects.size() - 1; i++) {
      checkObj(areaObjects.get(i), areaObjects.subList(i + 1, areaObjects.size()));
    }

  }

  public void addGameObjects(GameObject current, List<GameObject> allGameObjects) {
    allGameObjects.add(current);
    for (GameObject gameObject : current.getChildren()) {
      addGameObjects(gameObject, allGameObjects);
    }
  }

  public void checkObj(GameObject obj, List<GameObject> others) {

    Set<String> thisCollisionIgnores = obj.getOrDefault("collisionIgnore", Collections.emptySet());
    boolean isStatic = obj.getOrDefault("static", false);
    for (GameObject other : others) {
      // if both objects are static, don't check them
      if (isStatic && Boolean.TRUE.equals(other.getOrDefault("static", false))) {
        continue;
      }

      if (other.getBoundingBox() == null) {
        // cannot check objects without bounding boxes
        continue;
      }
      Rect thisBoundingBox = obj.getBoundingBox();
      // should we ignore the other object based on tags?
      Optional<String> ignoreTag = thisCollisionIgnores.stream()
          .filter(other::is)
          .findFirst();
      if (ignoreTag.isPresent()) {
        continue;
      }

      Vec2 res = collides(thisBoundingBox, other.getBoundingBox());
      if (res != null) {
        Collision col1 = new Collision(obj, other, res);
        obj.trigger(PHYSICS_COLLIDE_UPDATE, obj, col1, other);
        Collision col2 = col1.reverse();
        // resolution only has to happen only once
        col2.setResolved(col1.isResolved());
        other.trigger(PHYSICS_COLLIDE_UPDATE, other, col2, obj);
      }
    }
  }

  public <E extends Event> void on(String event, EventListener<E> listener) {
    eventManager.addEventListener(event, listener);
  }

  public <E extends Event> void on(String event, EventListener<E> listener, Object owner) {
    eventManager.addEventListener(event, listener, owner);
  }


  public void trigger(String event, Object... params) {
    eventManager.trigger(event, Arrays.asList(params));
  }

  /**
   * Update all game objects
   */
  public void onUpdate(String tag, EventListener<UpdateEvent> eventListener) {
    on(CoreEvents.UPDATE, (UpdateEvent event) -> {
      // only propagate event when the object has the required tag
      if (event.getSource() != null && event.getSource().is(tag)) {
        eventListener.onEvent(event);
      }
    });
  }

  /**
   * removes the game object and all of its children from the game.
   */
  public void destroy(GameObject gameObject) {
    gameObject.destroy();
    gameObject.getParent().remove(gameObject);
  }

  public void clearEvents() {
    eventManager.clear();
  }

  public void removeListener(Object owner) {
    eventManager.removeListener(owner);
  }
}