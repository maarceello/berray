package com.berray.components;

import com.berray.GameObject;

import static com.raylib.Jaylib.Vector2;

public class PosComponent extends Component {
  private final Vector2 pos;

  // Constructor
  public PosComponent(Vector2 pos) {
    super("pos");
    this.pos = pos;
  }

  // Getter
  public Vector2 getPos() {
    return pos;
  }

  // Static method to just call "pos()"
  public static PosComponent pos(int x, int y) {
    return new PosComponent(new Vector2(x, y));
  }

  @Override
  public void add(GameObject gameObject) {
    gameObject.addMethod("pos", this::getPos);
  }
}
