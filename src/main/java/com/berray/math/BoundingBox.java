package com.berray.math;

import com.raylib.Raylib;

public class BoundingBox {
  private Vec3 min;
  private Vec3 max;

  public BoundingBox() {
    this(Vec3.origin(), Vec3.origin());
  }

  public BoundingBox(Vec3 min, Vec3 max) {
    this.min = min;
    this.max = max;
  }

  public Vec3 getMin() {
    return min;
  }

  public Vec3 getMax() {
    return max;
  }

  public BoundingBox add(BoundingBox other) {
    Vec3 min = new Vec3(
        Math.min(this.min.x, other.min.x),
        Math.min(this.min.y, other.min.y),
        Math.min(this.min.z, other.min.z)
    );
    Vec3 max = new Vec3(
        Math.max(this.max.x, other.max.x),
        Math.max(this.max.y, other.max.y),
        Math.max(this.max.z, other.max.z)
    );
    return new BoundingBox(min, max);
  }

  public BoundingBox add(Raylib.BoundingBox other) {
    Vec3 min = new Vec3(
        Math.min(this.min.x, other.min().x()),
        Math.min(this.min.y, other.min().y()),
        Math.min(this.min.z, other.min().z())
    );
    Vec3 max = new Vec3(
        Math.max(this.max.x, other.max().x()),
        Math.max(this.max.y, other.max().y()),
        Math.max(this.max.z, other.max().z())
    );
    return new BoundingBox(min, max);
  }

}
