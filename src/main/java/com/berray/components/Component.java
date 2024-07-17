package com.berray.components;

import com.berray.GameObject;

public class Component {
  private int id;

  public void setId(int id) {
    this.id = id;
  }
  public int getId() {
    return id;
  }

  // Static methods have no this in their scope

  public void draw(GameObject gameObject) {}
  public void update(float deltaTime) {}
}
