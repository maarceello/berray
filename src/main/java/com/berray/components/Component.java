package com.berray.components;

public class Component {
  private int id;
  private final int type;

  public Component(int type) {
    this.type = type;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getType() {
    return type;
  }

  public int getId() {
    return id;
  }
}
