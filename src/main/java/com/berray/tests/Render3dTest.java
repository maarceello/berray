package com.berray.tests;

import com.berray.BerrayApplication;
import com.berray.GameObject;
import com.berray.components.CoreComponentShortcuts;
import com.berray.components.core.Component;
import com.berray.components.core.Rotate3dComponent;
import com.berray.event.UpdateEvent;
import com.berray.math.Color;
import com.berray.math.Quaternion;
import com.berray.math.Vec3;
import com.berray.components.core.CameraComponent;
import com.berray.objects.Root3D;
import com.raylib.Jaylib;
import com.raylib.Raylib;

import static com.raylib.Raylib.*;

public class Render3dTest extends BerrayApplication implements CoreComponentShortcuts {


  private Raylib.Camera3D camera;

  private float angleY = 0.0f;

  @Override
  public void game() {

    targetFps = -1;
    Root3D root3d = add(
        new Root3D(),
        pos(new Vec3(0.0f, 0.0f, 0.0f)),
        Rotate3dComponent.rotate(Quaternion.identity()),
        CameraComponent.camera(camera),
        "root3d"
    );

    root3d.add(
        new CubeComponent()
    );

    onUpdate("root3d", (UpdateEvent event) -> {
      GameObject gameObject = event.getSource();
      float deltaTime = event.getFrametime();
      angleY += 40 * deltaTime;
      gameObject.set("quaternion", Quaternion.fromEuler(0.0f, (float) Math.toRadians(angleY), 0.0f));
    });

    addFpsLabel();

  }

  @Override
  public void initWindow() {
    width(500);
    height(500);
    background(Color.GRAY);
    title("3D Render Mode Test");

    this.camera = new Raylib.Camera3D();
    this.camera._position(new Jaylib.Vector3(0.0f, 10.0f, 10.0f));  // Camera position
    this.camera.target(new Jaylib.Vector3(0.0f, 0.0f, 0.0f));      // Camera looking at point
    this.camera.up(new Jaylib.Vector3(0.0f, 1.0f, 0.0f));          // Camera up vector (rotation towards target)
    this.camera.fovy(45.0f);                                // Camera field-of-view Y
    this.camera.projection(Raylib.CAMERA_PERSPECTIVE);             // Camera mode type
  }


  public static void main(String[] args) {
    new Render3dTest().runGame();
  }

  public static class CubeComponent extends Component {
    public CubeComponent() {
      super("cube");
    }

    @Override
    public void add(GameObject gameObject) {
      super.add(gameObject);
      registerGetter("render", () -> true);
    }

    @Override
    public void draw() {
      Raylib.DrawCubeWires(new Vector3(), 1.0f,1.0f,1.0f, Jaylib.WHITE);
    }
  }
}
