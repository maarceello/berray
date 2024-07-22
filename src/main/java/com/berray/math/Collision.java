package com.berray.math;

import com.berray.GameObject;

public class Collision {
  private GameObject obj;
  private GameObject other;
  /** Vector the object must be displaced to resolve the collision. */
  private Vec2 displacement;

  /** True when the collision is resolved. */
  private boolean resolved = false;

  public Collision(GameObject obj, GameObject other, Vec2 res) {
    this.obj = obj;
    this.other = other;
    this.displacement = res;
  }

  public GameObject getObj() {
    return obj;
  }

  public void setObj(GameObject obj) {
    this.obj = obj;
  }

  public GameObject getOther() {
    return other;
  }

  public void setOther(GameObject other) {
    this.other = other;
  }

  public Vec2 getDisplacement() {
    return displacement;
  }

  public void setDisplacement(Vec2 displacement) {
    this.displacement = displacement;
  }

  public boolean isResolved() {
    return resolved;
  }

  public void setResolved(boolean resolved) {
    this.resolved = resolved;
  }

  public Collision reverse() {
    return new Collision(other, obj, new Vec2(-displacement.getX(), -displacement.getY()));
  }
}
