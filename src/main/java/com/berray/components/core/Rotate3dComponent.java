package com.berray.components.core;

import com.berray.GameObject;
import com.berray.math.Matrix4;

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

  private float getRotateX() {
    return rotateX;
  }

  private void setRotateX(float rotateX) {
    this.rotateX = clamp(rotateX);
    gameObject.setTransformDirty();
  }

  private float getRotateY() {
    return rotateY;
  }

  private void setRotateY(float rotateY) {
    this.rotateY = clamp(rotateY);
    gameObject.setTransformDirty();
  }

  private float getRotateZ() {
    return rotateZ;
  }

  private void setRotateZ(float rotateZ) {
    this.rotateZ = clamp(rotateZ);
    gameObject.setTransformDirty();
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

  public Matrix4 getRotationMatrix() {
    return Matrix4.identity()
        .multiply(Matrix4.fromRotateX((float) Math.toRadians(rotateX)))
        .multiply(Matrix4.fromRotateY((float) Math.toRadians(rotateY)))
        .multiply(Matrix4.fromRotateZ((float) Math.toRadians(rotateZ)));
  }

  @Override
  public void add(GameObject gameObject) {
    registerBoundProperty("angleX", this::getRotateX, this::setRotateX);
    registerBoundProperty("angleY", this::getRotateY, this::setRotateY);
    registerBoundProperty("angleZ", this::getRotateZ, this::setRotateZ);
    registerGetter("rotationMatrix", this::getRotationMatrix);
  }

  public static Rotate3dComponent rotate() {
    return new Rotate3dComponent(0, 0, 0);
  }

  public static Rotate3dComponent rotate(float rotateX, float rotateY, float rotateZ) {
    return new Rotate3dComponent(rotateX, rotateY, rotateZ);
  }

}
