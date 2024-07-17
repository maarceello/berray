package com.berray.components;

import com.berray.GameObject;
import static com.raylib.Jaylib.*;

public class RotateComponent extends Component {
  private final float angle;

  public RotateComponent(float angle) {
    this.angle = angle;
  }

  public float getAngle() {
    return angle;
  }

  public static RotateComponent rotate(float angle) {
    return new RotateComponent(angle);
  }
}
