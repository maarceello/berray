package com.berray.components;

import com.berray.GameObject;
import com.berray.math.Vec2;

public class PosComponent extends Component {
  private final Vec2 pos;

  // Constructor
  public PosComponent(Vec2 pos) {
    super("pos");
    this.pos = pos;
  }

  // Getter
  public Vec2 getPos() {
    return pos;
  }

  // Static method to just call "pos()"
  public static PosComponent pos(int x, int y) {
    return new PosComponent(new Vec2(x, y));
  }

  @Override
  public void add(GameObject gameObject) {
    gameObject.addMethod("pos", this::getPos);
  }
}
