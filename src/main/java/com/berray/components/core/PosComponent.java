package com.berray.components.core;

import com.berray.GameObject;
import com.berray.math.Vec2;

import java.util.function.Consumer;

public class PosComponent extends Component {
  private Vec2 pos;

  // Constructor
  public PosComponent(Vec2 pos) {
    super("pos");
    this.pos = pos;
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

  @Override
  public void add(GameObject gameObject) {
    gameObject.registerMethod("pos", this::getPos, this::setPos);
  }

}
