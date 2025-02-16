package com.berray.tests;

import com.berray.BerrayApplication;
import com.berray.GameObject;
import com.berray.components.CoreComponentShortcuts;
import com.berray.components.core.CameraComponent;
import com.berray.components.core.Component;
import com.berray.components.core.Rotate3dComponent;
import com.berray.event.UpdateEvent;
import com.berray.math.Color;
import com.berray.math.Quaternion;
import com.berray.math.Vec3;
import com.berray.objects.RenderToTexture;
import com.berray.objects.Root3D;
import com.raylib.Raylib;

import java.util.function.Supplier;

public class RenderToTextureTest extends BerrayApplication implements CoreComponentShortcuts {
  private Raylib.Camera3D camera;

  private float angleY = 0.0f;

  @Override
  public void game() {
    targetFps = -1;


    Root3D root3d = make(
        new Root3D(),
        pos(new Vec3(0.0f, 0.0f, 0.0f)),
        Rotate3dComponent.rotate(Quaternion.identity()),
        CameraComponent.camera(camera),
        "root3d"
    );

    root3d.add(
        new Render3dTest.CubeComponent()
    );


    RenderToTexture renderToTexture = add(
        new RenderToTexture(width(), height())
    );
    renderToTexture.add(root3d);


    add(
        new RenderTextureSpriteComponent(renderToTexture::getRenderTexture),
        pos(0,0)
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
    this.camera._position(new Raylib.Vector3().x(0.0f).y(10.0f).z( 10.0f));  // Camera position
    this.camera.target(new Raylib.Vector3().x(0.0f).y( 0.0f).z( 0.0f));      // Camera looking at point
    this.camera.up(new Raylib.Vector3().x(0.0f).y( 1.0f).z( 0.0f));          // Camera up vector (rotation towards target)
    this.camera.fovy(45.0f);                                // Camera field-of-view Y
    this.camera.projection(Raylib.CAMERA_PERSPECTIVE);             // Camera mode type
  }

  public static void main(String[] args) {
    new RenderToTextureTest().runGame();
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
      Raylib.DrawCubeWires(new Raylib.Vector3(), 1.0f, 1.0f, 1.0f, Color.WHITE.toRaylibColor());
    }
  }

  public static class RenderTextureSpriteComponent extends Component {

    private final Supplier<Raylib.RenderTexture> renderTextureGetter;

    public RenderTextureSpriteComponent(Supplier<Raylib.RenderTexture> renderTextureGetter) {
      super("RenderTextureSpriteComponent");
      this.renderTextureGetter = renderTextureGetter;
    }

    @Override
    public void add(GameObject gameObject) {
      super.add(gameObject);
      registerGetter("render", () -> true);
    }

    @Override
    public void draw() {
      Raylib.DrawTexture(renderTextureGetter.get().texture(), 0,0, Color.WHITE.toRaylibColor());
    }

  }

}
