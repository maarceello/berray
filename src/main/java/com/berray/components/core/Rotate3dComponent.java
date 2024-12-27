package com.berray.components.core;

import com.berray.GameObject;
import com.berray.math.Matrix4;
import com.berray.math.Quaternion;

/**
 * Rotate the game object and all child objects in 3d.
 */
public class Rotate3dComponent extends Component {

  private Quaternion quaternion;

  public Rotate3dComponent(Quaternion quaternion) {
    super("rotate");
    this.quaternion = quaternion;
  }

  @Override
  public void add(GameObject gameObject) {
    registerBoundProperty("quaternion", this::getQuaternion, this::setQuaternion);
    registerGetter("rotationMatrix", this::getRotationMatrix);
  }

  /**
   * Sets the rotation quaternion.
   *
   * @type property
   */
  public Quaternion getQuaternion() {
    return quaternion;
  }

  /**
   * Sets the rotation quaternion.
   *
   * @type property
   */
  public void setQuaternion(Quaternion quaternion) {
    this.quaternion = quaternion;
    gameObject.setTransformDirty();
  }

  /**
   * Returns the rotation matrix
   *
   * @type property
   */
  public Matrix4 getRotationMatrix() {
    return quaternion.toMatrix();
  }

  /**
   * Create rotation component with initial rotation described by the quaternion.
   *
   * @type creator
   */
  public static Rotate3dComponent rotate(Quaternion quaternion) {
    return new Rotate3dComponent(quaternion);
  }
}
