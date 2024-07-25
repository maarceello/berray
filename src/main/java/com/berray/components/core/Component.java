package com.berray.components.core;

import com.berray.GameObject;

public class Component {
  /** "name" of the component, also used as a tag. */
  private String tag;
  private int id;

  /** Gameobject this component is added to. */
  protected GameObject gameObject;

  public Component(String tag) {
    this.tag = tag;
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
  }
}
