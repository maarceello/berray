package com.berray.components.core;

import com.berray.GameObject;
import com.berray.math.Vec2;

import java.util.List;

public class PosComponent extends Component {
  private Vec2 pos;

  // Constructor
  public PosComponent(Vec2 pos) {
    super("pos");
    this.pos = pos;
  }

  @Override
  public void add(GameObject gameObject) {
    gameObject.registerMethod("pos", this::getPos, this::setPos);
    gameObject.registerAction("move", this::move);
    gameObject.registerAction("moveBy", this::moveBy);
  }

  /**
   * params:
   * - vec2 velocity
   * - float deltaTime
   * */
  public void move(List<Object> params) {
    if (params.get(0) instanceof Vec2) {
      Vec2 velocity = (Vec2) params.get(0);
      float deltaTime = (float) params.get(1);
      pos = pos.move(velocity.scale(deltaTime));
    }
  }

  /**
   * params:
   * - vec2 amount
   * */
  public void moveBy(List<Object> params) {
    if (params.get(0) instanceof Vec2) {
      Vec2 velocity = (Vec2) params.get(0);
      pos = pos.move(velocity);
    }
  }

  // Getter
  public Vec2 getPos() {
    return pos;
  }

  public void setPos(Vec2 pos) {
    this.pos = pos;
    this.gameObject.setTransformDirty();
  }

  // Static method to just call "pos()"
  public static PosComponent pos(float x, float y) {
    return new PosComponent(new Vec2(x, y));
  }
  public static PosComponent pos(Vec2 pos) {
    return new PosComponent(pos);
  }


}
