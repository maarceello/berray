package com.berray.components.core;

import com.berray.Game;
import com.berray.GameObject;
import com.berray.assets.AssetManager;
import com.berray.event.Event;
import com.berray.event.EventListener;
import com.berray.event.PropertyChangeEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Component {
  /**
   * unique id
   */
  private int id;
  /**
   * "name" of the component, also used as a tag.
   */
  private String tag;
  /**
   * required components of
   */
  private final String[] dependencies;

  /** False when configuration is still allowed. Will be set to true one the component is added to a game object. */
  protected boolean configurationLocked = false;


  private Set<String> properties = new HashSet<>();
  private Set<String> actions = new HashSet<>();

  /**
   * Gameobject this component is added to.
   */
  protected GameObject gameObject;

  public Component(String tag, String... dependencies) {
    this.tag = tag;
    this.dependencies = dependencies;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public void setGameObject(GameObject gameObject) {
    this.gameObject = gameObject;
  }

  public String getTag() {
    return tag;
  }

  // Static methods have no this in their scope

  /**
   * Method to draw the component. May be overridden by subclasses.
   */
  public void draw() {
    // May be implemented by subclasses
  }

  /** Returns where to allows to add this component multiple times to a game object. Default: no, only once. */
  public boolean allowMultiple() {
    return false;
  }

  protected AssetManager getAssetManager() {
    if (gameObject == null) {
      throw new IllegalStateException("component is not added to game object");
    }
    Game game = gameObject.getGame();
    if (game == null) {
      throw new IllegalStateException("game object is not part of game tree");
    }
    return game.getAssetManager();
  }

  /**
   * Method called when the component is added to the game object.
   * May be overridden by subclasses, but remember to call `super.add(gameObject);`.
   */
  public void add(GameObject gameObject) {
    this.gameObject = gameObject;
    this.configurationLocked = true;
    // check requirements
    for (String dependency : dependencies) {
      if (!gameObject.is(dependency)) {
        throw new IllegalStateException("component " + tag + " requires " + dependency + ", but game object has only " + gameObject.getTags());
      }
    }
  }

  protected void checkConfigurationAllowed() {
    if (configurationLocked) {
      throw new IllegalStateException("Configuration for component "+getTag()+" is locked, as it was already added to a game object.");
    }
  }


  /**
   * Registers a property, remembering the property name. Upon deletion the properties will be removed.
   */
  public <E> void registerProperty(String name, Supplier<E> getter, Consumer<E> setter) {
    gameObject.registerProperty(name, getter, setter);
    properties.add(name);
  }

  /**
   * Registers a property which triggers a <code>propertyChange</code> event when the property is changed.
   * The property name is remembered. Upon deletion the properties will be removed.
   */
  public <E> void registerBoundProperty(String name, Supplier<E> getter, Consumer<E> setter) {
    gameObject.registerBoundProperty(name, getter, setter);
    properties.add(name);
  }


  /**
   * Registers a getter, remembering the property name. Upon deletion the properties
   * will be removed.
   */
  public void registerGetter(String name, Supplier<?> getter) {
    gameObject.registerPropertyGetter(name, getter);
    properties.add(name);
  }

  /**
   * Registers a setter, remembering the property name. Upon deletion the properties
   * will be removed.
   */
  public <E> void registerSetter(String name, Consumer<E> setter) {
    gameObject.registerPropertySetter(name, setter);
    properties.add(name);
  }

  /**
   * Registers a property setter which triggers a <code>propertyChange</code> event when the property is changed.
   * The property name is remembered. Upon deletion the properties will be removed.
   * <p>
   * Note: to compare the old and the new value the getter is needed too.
   */
  public <E> void registerBoundSetter(String name, Supplier<E> getter, Consumer<E> setter) {
    registerBoundSetter(name, getter, setter);
    properties.add(name);
  }

  /**
   * Registers an action, remembering the action name. Upon deletion the action will be removed.
   */
  public <E> void registerAction(String name, Consumer<E> actionMethod, Function<List<Object>, E> actionBeanCreator) {
    gameObject.registerAction(name, (List<Object> params) -> actionMethod.accept(actionBeanCreator.apply(params)));
    actions.add(name);
  }

  /**
   * Registers an action, remembering the action name. Upon deletion the action will be removed.
   */
  public <E> void registerAction(String name, Function<E, ?> actionMethod, Function<List<Object>, E> actionBeanCreator) {
    gameObject.registerAction(name, (List<Object> params) -> actionMethod.apply(actionBeanCreator.apply(params)));
    actions.add(name);
  }

  /**
   * Registers an action, remembering the action name. Upon deletion the action will be removed.
   */
  public void registerAction(String name, Runnable actionMethod) {
    gameObject.registerAction(name, actionMethod);
    actions.add(name);
  }

  /**
   * Registers an event listener. Upon deletion the event listener will be removed.
   */
  public <E extends Event> void on(String eventName, EventListener<E> eventListener) {
    gameObject.on(eventName, eventListener, this);
  }

  /**
   * Registers an event listener. Upon deletion the event listener will be removed.
   */
  public <E extends PropertyChangeEvent> void onPropertyChange(String propertyName, EventListener<E> eventListener) {
    gameObject.onPropertyChange(propertyName, eventListener, this);
  }

  /**
   * Registers an event listener on the game obeject. Upon deletion the event listener will be removed.
   */
  public <E extends Event> void onGame(String eventName, EventListener<E> eventListener) {
    gameObject.getGame().on(eventName, eventListener, this);
  }

  /**
   * Method called when the component is removed from the game object. This is also called when the game
   * object is removed. May be overridden by subclasses, but remember to call `super.destroy();`.
   */
  public void destroy() {
    properties.forEach(gameObject::removeProperty);
    properties.clear();

    actions.forEach(gameObject::removeAction);
    actions.clear();

    gameObject.removeListener(this);
  }

}
