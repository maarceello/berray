package com.berray.components.core;

import com.berray.GameObject;
import com.berray.math.Matrix4;

/**
 * Rotate the game object and all child objects around the z axis.
 */
public class RotateComponent extends Component {
  private float angle;

  public RotateComponent(float angle) {
    super("rotate");
    this.angle = angle;
  }

  @Override
  public void add(GameObject gameObject) {
    registerBoundProperty("angle", this::getAngle, this::setAngle);
    registerGetter("rotationMatrix", this::getRotationMatrix);
  }

  /**
   * Returns the angle of the rotation
   *
   * @type property
   */
  public float getAngle() {
    return angle;
  }

  /**
   * Sets the angle of the rotation
   *
   * @type property
   */
  public void setAngle(float angle) {
    if (angle > 360) {
      angle = angle % 360;
    }
    if (angle < 0) {
      angle += 360;
    }
    this.angle = angle;
    gameObject.setTransformDirty();
  }

  /**
   * Returns the rotation matrix.
   *
   * @type property
   */
  private Matrix4 getRotationMatrix() {
    return Matrix4.fromRotateZ((float) Math.toRadians(angle));
  }

  /** Creates a new rotation component with an initial angle. */
  public static RotateComponent rotate(float angle) {
    return new RotateComponent(angle);
  }
}
