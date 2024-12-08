package com.berray.components.core;

import com.berray.GameObject;
import com.raylib.Raylib;

public class CameraComponent extends Component {

  private Raylib.Camera3D camera;
  public CameraComponent(Raylib.Camera3D camera) {
    super("camera");
    this.camera = camera;
  }

  @Override
  public void add(GameObject gameObject) {
    super.add(gameObject);
    registerBoundProperty("camera", this::getCamera, this::setCamera);
  }

  public Raylib.Camera3D getCamera() {
    return camera;
  }

  public void setCamera(Raylib.Camera3D camera) {
    this.camera = camera;
  }

  public static CameraComponent camera(Raylib.Camera3D camera) {
    return new CameraComponent(camera);
  }
}
