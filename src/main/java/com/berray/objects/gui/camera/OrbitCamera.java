package com.berray.objects.gui.camera;

import com.berray.GameObject;
import com.berray.event.*;
import com.berray.math.MathUtil;
import com.berray.math.Vec2;
import com.raylib.Raylib;

public class OrbitCamera extends GameObject {
  /** Angle up/down */
  private float pitch = 45;
  /** Angle around up axis (in this case the y axis) */
  private float yaw;
  /** Distance of the camera from the lookat center. */
  private float distance = 20;
  /** Low Level camera from raylib. */
  private Raylib.Camera3D camera;

  /** Position the drag started. */
  private Vec2 dragStart;
  private float pitchStart;
  private float yawStart;

  public OrbitCamera(Raylib.Camera3D camera) {
    this.camera = camera;
    on(CoreEvents.SCENE_GRAPH_ADDED, this::onSceneGraphAdded);
  }

  private  void onSceneGraphAdded(SceneGraphEvent e) {
    game.on(CoreEvents.MOUSE_MOVE, this::processMouseMove, this);
    game.on(CoreEvents.MOUSE_WHEEL_MOVE, this::processMouseWheelMove, this);
    game.on(CoreEvents.MOUSE_PRESS, this::processMousePress, this);
    game.on(CoreEvents.MOUSE_RELEASE, this::processMouseRelease, this);
  }

  private void processMouseWheelMove(MouseWheelEvent e) {
    distance += e.getWheelDelta();
    updateCameraPosition();
  }

  private void processMouseMove(MouseEvent e) {
    if (dragStart == null) {
      return;
    }
    Vec2 delta = e.getWindowPos().sub(dragStart);
    this.yaw = yawStart + delta.getX();
    this.pitch = MathUtil.clamp(pitchStart + delta.getY(), 0, 90);

    updateCameraPosition();
  }

  private void updateCameraPosition() {
    double yawRadians = Math.toRadians(this.yaw);
    double pitchRadians = Math.toRadians(this.pitch);


    float x = (float) (Math.cos(-pitchRadians) * Math.cos(yawRadians));
    float y = (float) (Math.sin(-pitchRadians));
    float z = (float) (Math.cos(-pitchRadians) * Math.sin(yawRadians));

    camera._position().x(x * distance);
    camera._position().y(y * distance);
    camera._position().z(z * distance);
  }

  private void processMousePress(MouseEvent e) {
    dragStart = e.getWindowPos();
    pitchStart = pitch;
    yawStart = yaw;
  }
  private void processMouseRelease(MouseEvent e) {
    dragStart = null;
  }


}
