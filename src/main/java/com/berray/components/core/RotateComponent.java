package com.berray.components.core;

import com.berray.GameObject;

public class RotateComponent extends Component {
  private float angle;

  public RotateComponent(float angle) {
    super("rotate");
    this.angle = angle;
  }

  public float getAngle() {
    return angle;
  }

  public void setAngle(float angle ) {
    if (angle > 360) {
      angle = angle % 360;
    }
    this.angle = angle;
    gameObject.setTransformDirty();
  }

  @Override
  public void add(GameObject gameObject) {
    registerBoundProperty("angle", this::getAngle, this::setAngle);
  }

  public static RotateComponent rotate(float angle) {
    return new RotateComponent(angle);
  }
}
