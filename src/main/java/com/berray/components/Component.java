package com.berray.components;

import com.berray.GameObject;

public class Component {
  /** "name" of the component, also used as a tag. */
  private String tag;
  private int id;

  public Component(String tag) {
    this.tag = tag;
  }

  public void setId(int id) {
    this.id = id;
  }
  public int getId() {
    return id;
  }

  // Static methods have no this in their scope

  public void draw(GameObject gameObject) {}
  public void update(float deltaTime) {}

  public void add(GameObject gameObject) {}
}
