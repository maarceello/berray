package com.berray.components.core;

import com.berray.GameObject;
import com.berray.math.Vec3;

public class ScaleComponent extends Component {
  private Vec3 scale;

  public ScaleComponent(float scaleX, float scaleY, float scaleZ) {
    super("scale");
    this.scale = new Vec3(scaleX, scaleY, scaleZ);
  }

  @Override
  public void add(GameObject gameObject) {
    super.add(gameObject);
    registerGetter("scale", this::getScale);
  }

  public Vec3 getScale() {
    return scale;
  }

  public static ScaleComponent scale(float scale) {
    return new ScaleComponent(scale, scale, 1.0f);
  }

  public static ScaleComponent scale(float scaleX, float scaleY, float scaleZ) {
    return new ScaleComponent(scaleX, scaleY, scaleZ);
  }
}
