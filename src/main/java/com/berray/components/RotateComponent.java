package com.berray.components;

import com.berray.GameObject;
import static com.raylib.Jaylib.*;

public class RotateComponent extends Component {
  private final float angle;

  public RotateComponent(float angle) {
    super("rotate");
    this.angle = angle;
  }

  public float getAngle() {
    return angle;
  }

  @Override
  public void add(GameObject gameObject) {
    gameObject.registerGetter("angle", this::getAngle);
  }

  public static RotateComponent rotate(float angle) {
    return new RotateComponent(angle);
  }
}
