package com.berray.components.core;

import com.berray.GameObject;
import com.berray.math.Matrix4;
import com.berray.math.Quaternion;
import com.berray.math.Vec3;

/**
 * Rotate the game object and all child objects in 3d.
 */
public class Rotate3dComponent extends Component {

  private float rotateX;
  private float rotateY;
  private float rotateZ;

  public Rotate3dComponent(float rotateX, float rotateY, float rotateZ) {
    super("rotate");
    this.rotateX = rotateX;
    this.rotateY = rotateY;
    this.rotateZ = rotateZ;
  }

  @Override
  public void add(GameObject gameObject) {
    registerBoundProperty("angleX", this::getRotateX, this::setRotateX);
    registerBoundProperty("angleY", this::getRotateY, this::setRotateY);
    registerBoundProperty("angleZ", this::getRotateZ, this::setRotateZ);
    registerGetter("rotationMatrix", this::getRotationMatrix);
  }

  /**
   * Returns the x angle of the rotation
   *
   * @type property
   */
  private float getRotateX() {
    return rotateX;
  }

  /**
   * Sets the x angle of the rotation
   *
   * @type property
   */
  private void setRotateX(float rotateX) {
    this.rotateX = clamp(rotateX);
    gameObject.setTransformDirty();
  }

  /**
   * Returns the y angle of the rotation
   *
   * @type property
   */
  private float getRotateY() {
    return rotateY;
  }

  /**
   * Sets the y angle of the rotation
   *
   * @type property
   */
  private void setRotateY(float rotateY) {
    this.rotateY = clamp(rotateY);
    gameObject.setTransformDirty();
  }

  /**
   * Returns the z angle of the rotation
   *
   * @type property
   */
  private float getRotateZ() {
    return rotateZ;
  }

  /**
   * Sets the x angle of the rotation
   *
   * @type property
   */
  private void setRotateZ(float rotateZ) {
    this.rotateZ = clamp(rotateZ);
    gameObject.setTransformDirty();
  }

  /**
   * Returns the rotation matrix
   *
   * @type property
   */
  public Matrix4 getRotationMatrix() {
    return Matrix4.identity()
        .multiply(Matrix4.fromRotateX((float) Math.toRadians(rotateX)))
        .multiply(Matrix4.fromRotateY((float) Math.toRadians(rotateY)))
        .multiply(Matrix4.fromRotateZ((float) Math.toRadians(rotateZ)));
  }

  private float clamp(float angle) {
    if (angle > 360) {
      angle = angle % 360;
    }
    if (angle < 0) {
      angle += 360;
    }
    return angle;
  }

  /**
   * Create rotation component with no initial rotation.
   *
   * @type creator
   */
  public static Rotate3dComponent rotate() {
    return new Rotate3dComponent(0, 0, 0);
  }

  /**
   * Create rotation component with initial rotation around the euler angles.
   *
   * @type creator
   */
  public static Rotate3dComponent rotate(float rotateX, float rotateY, float rotateZ) {
    return new Rotate3dComponent(rotateX, rotateY, rotateZ);
  }

  /**
   * Create rotation component with initial rotation described by the quaternion.
   *
   * @type creator
   */
  public static Rotate3dComponent rotate(Quaternion quaternion) {
    Vec3 euler = quaternion.toEuler();
    return new Rotate3dComponent(euler.getX(), euler.getY(), euler.getZ());
  }
}
