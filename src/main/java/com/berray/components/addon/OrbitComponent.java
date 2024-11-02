package com.berray.components.addon;

import com.berray.GameObject;
import com.berray.components.core.Component;
import com.berray.event.UpdateEvent;
import com.berray.math.Vec2;

import static com.berray.event.CoreEvents.UPDATE;

/** changes the position of the game object so it orbits its parent. */
public class OrbitComponent extends Component {
  private final float radius;
  private final float angleSpeed;

  private float currentAngle = 0;

  public OrbitComponent(float radius, float angleSpeed, float initialAngle) {
    super("orbit");
    this.radius = radius;
    this.angleSpeed = angleSpeed;
    this.currentAngle = initialAngle;
  }

  @Override
  public void add(GameObject gameObject) {
    gameObject.on(UPDATE, this::update);
  }

  public void update(UpdateEvent event) {
    float deltaTime = event.getFrametime();
    this.currentAngle += ((deltaTime * angleSpeed) + 360) % 360;
    float x = (float) (radius * Math.sin(Math.toRadians(currentAngle)));
    float y = (float) (radius * Math.cos(Math.toRadians(currentAngle)));
    gameObject.set("pos", new Vec2(-x,y));
  }

  public static OrbitComponent orbit(float radius, float angleSpeed) {
    return new OrbitComponent(radius, angleSpeed, 0);
  }

  public static OrbitComponent orbit(float radius, float angleSpeed, float initialAngle) {
    return new OrbitComponent(radius, angleSpeed, initialAngle);
  }
}
