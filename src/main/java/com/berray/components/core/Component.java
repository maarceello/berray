package com.berray.components.core;

import com.berray.GameObject;

public class Component {
  /** unique id */
  private int id;
  /** "name" of the component, also used as a tag. */
  private String tag;
  /** required components of */
  private final String[] dependencies;

  /** Gameobject this component is added to. */
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

  public void draw() {}

  public void add(GameObject gameObject) {
    this.gameObject = gameObject;
    // check requirements
    for (String dependency : dependencies) {
      if (!gameObject.is(dependency)) {
        throw new IllegalStateException("component " + tag + " requires "+dependency+", but game object has only "+gameObject.getTags());
      }
    }
  }

}
