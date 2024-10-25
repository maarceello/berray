package com.berray.tests;

import com.berray.BerrayApplication;
import com.berray.GameObject;
import com.berray.components.CoreComponentShortcuts;
import com.berray.components.core.Component;
import com.berray.components.core.Rotate3dComponent;
import com.berray.event.UpdateEvent;
import com.berray.math.Color;
import com.berray.math.Vec3;
import com.berray.objects.CameraComponent;
import com.berray.objects.Root3D;
import com.raylib.Jaylib;
import com.raylib.Raylib;

import static com.raylib.Raylib.*;

public class Render3dTest extends BerrayApplication implements CoreComponentShortcuts {


  private Raylib.Camera3D camera;

  @Override
  public void game() {

    targetFps = -1;
    Root3D root3d = add(
        new Root3D(),
        pos(new Vec3(0.0f, 0.0f, 0.0f)),
        Rotate3dComponent.rotate(),
        CameraComponent.camera(camera),
        "root3d"
    );

    root3d.add(
        new CubeComponent()
    );

    onUpdate("root3d", (UpdateEvent event) -> {
      GameObject gameObject = event.getSource();
      float deltaTime = event.getFrametime();
      float angle = gameObject.get("angleY");
      angle += 40 * deltaTime;
      gameObject.set("angleY", angle);
    });

    addFpsLabel();

  }

  @Override
  public void initWindow() {
    width(500);
    height(500);
    background(Color.GRAY);
    title("3D Render Mode Test");

    Raylib.Camera3D camera = new Raylib.Camera3D();
    camera._position(new Jaylib.Vector3(0.0f, 10.0f, 10.0f));  // Camera position
    camera.target(new Jaylib.Vector3(0.0f, 0.0f, 0.0f));      // Camera looking at point
    camera.up(new Jaylib.Vector3(0.0f, 1.0f, 0.0f));          // Camera up vector (rotation towards target)
    camera.fovy(45.0f);                                // Camera field-of-view Y
    camera.projection(Raylib.CAMERA_PERSPECTIVE);             // Camera mode type

    this.camera = camera;
  }


  public static void main(String[] args) {
    new Render3dTest().runGame();
  }

  private static class CubeComponent extends Component {
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
      rlPushMatrix();
      {
          rlMultMatrixf(gameObject.getWorldTransform().toFloatTransposed());
          Raylib.DrawCubeWires(new Vector3(), 1.0f,1.0f,1.0f, Jaylib.WHITE);
      }
      rlPopMatrix();
    }
  }
}
