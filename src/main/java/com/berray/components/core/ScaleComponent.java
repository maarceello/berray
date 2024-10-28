package com.berray.components.core;

import com.berray.GameObject;
import com.berray.math.Vec2;
import com.berray.math.Vec3;

/**
 * Scales the game object and all children by a factor. Note that in 2d stack the
 * z factor must be 1.0
 */
public class ScaleComponent extends Component {
  private Vec3 scale;

  public ScaleComponent(float scaleX, float scaleY, float scaleZ) {
    super("scale");
    this.scale = new Vec3(scaleX, scaleY, scaleZ);
  }

  @Override
  public void add(GameObject gameObject) {
    super.add(gameObject);
    registerBoundProperty("scale", this::getScale, this::setScale);
  }

  /**
   * Returns the scale factors for each axis.
   *
   * @type property
   */
  public Object getScale() {
    return scale;
  }

  /**
   * Sets the scale factors for each axis.
   *
   * @type property
   */
  public void setScale(Object scale) {
    if (scale == null) {
      this.scale = new Vec3(1.0f, 1.0f, 1.0f);
    } else if (scale instanceof Vec3) {
      this.scale = (Vec3) scale;
    } else if (scale instanceof Vec2) {
      Vec2 vec2 = (Vec2) scale;
      this.scale = new Vec3(vec2.getX(), vec2.getY(), 1.0f);
    } else if (scale instanceof Float) {
      this.scale = new Vec3((Float) scale, (Float) scale, (Float) scale);
    } else {
      throw new IllegalArgumentException("Unknown Scale type: "+scale.getClass().getSimpleName()+". Supported are Float, Vec2 and Vec3");
    }
    gameObject.setTransformDirty();
  }

  /**
   * Creates a scale component with equal scale factor in x and y axis and a scale of 1.0 in z axis.
   *
   * @type creator
   */
  public static ScaleComponent scale(float scale) {
    return new ScaleComponent(scale, scale, 1.0f);
  }

  /**
   * Creates a scale component with initial factors in each axis.
   *
   * @type creator
   */
  public static ScaleComponent scale(float scaleX, float scaleY, float scaleZ) {
    return new ScaleComponent(scaleX, scaleY, scaleZ);
  }
}
