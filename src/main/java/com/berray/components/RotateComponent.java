package com.berray.components;

import com.berray.GameObject;

import java.util.function.Consumer;

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
    this.angle = angle;
    gameObject.setTransformDirty();
  }

  @Override
  public void add(GameObject gameObject) {
    gameObject.registerMethod("angle", this::getAngle, this::setAngle);
  }

  public static RotateComponent rotate(float angle) {
    return new RotateComponent(angle);
  }
}
