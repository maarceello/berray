package com.berray.components.core;

import com.berray.GameObject;

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
   * Method called when the component is removed from the game object. This is also called when the game
   * object is removed. May be overridden by subclasses, but remember to call `super.destroy();`.
   */
  public void destroy() {
    gameObject = null;
  }

}
