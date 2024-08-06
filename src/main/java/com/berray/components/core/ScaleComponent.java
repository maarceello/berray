package com.berray.components.core;

import com.berray.GameObject;

public class ScaleComponent extends Component {
  private float scale;

  public ScaleComponent(float scale) {
    super("scale");
    this.scale = scale;
  }

  @Override
  public void add(GameObject gameObject) {
    super.add(gameObject);
    registerGetter("scale", this::getScale);
  }

  public float getScale() {
    return scale;
  }

  public static ScaleComponent scale(float scale) {
    return new ScaleComponent(scale);
  }
}
