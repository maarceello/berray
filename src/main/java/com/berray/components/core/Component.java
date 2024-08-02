package com.berray.components.core;

import com.berray.GameObject;
import com.berray.event.EventListener;

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
  }

  /**
   * Method called when the component is added to the game object.
   * May be overridden by subclasses, but remember to call `super.add(gameObject);`.
   */
  public void add(GameObject gameObject) {
    this.gameObject = gameObject;
    // check requirements
    for (String dependency : dependencies) {
      if (!gameObject.is(dependency)) {
        throw new IllegalStateException("component " + tag + " requires " + dependency + ", but game object has only " + gameObject.getTags());
      }
    }
  }

  /**
   * Registers a method, remembering the property name. Upon deletion the properties will be removed.
   */
  public <E> void registerMethod(String name, Supplier<E> getter, Consumer<E> setter) {
    gameObject.registerMethod(name, getter, setter);
    properties.add(name);
  }

  /**
   * Registers a getter, remembering the property name. Upon deletion the properties
   * will be removed.
   */
  public void registerGetter(String name, Supplier<?> method) {
    gameObject.registerGetter(name, method);
    properties.add(name);
  }

  /**
   * Registers a setter, remembering the property name. Upon deletion the properties
   * will be removed.
   */
  public <E> void registerSetter(String name, Consumer<E> setter) {
    gameObject.registerSetter(name, setter);
    properties.add(name);
  }


  /**
   * Registers an action, remembering the action name. Upon deletion the action will be removed.
   */
  public void registerAction(String name, Consumer<List<Object>> actionMethod) {
    gameObject.registerAction(name, actionMethod);
    actions.add(name);
  }

  /**
   * Registers an action, remembering the action name. Upon deletion the action will be removed.
   */
  public void registerAction(String name, Function<List<Object>, ?> actionMethod) {
    gameObject.registerAction(name, actionMethod);
    actions.add(name);
  }


  /**
   * Registers an action, remembering the action name. Upon deletion the action will be removed.
   */
  public void on(String name, EventListener eventListener) {
    gameObject.on(name, eventListener, this);
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
