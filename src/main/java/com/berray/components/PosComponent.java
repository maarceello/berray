package com.berray.components;

import com.raylib.Jaylib.Vector2;

public class PosComponent extends Component {
  private final Vector2 pos;


  public Vector2 getPos() {
    return pos;
  }

  public PosComponent(Vector2 pos) {
    super(1);
    this.pos = pos;
  }

  public static PosComponent pos(int x, int y) {
    return new PosComponent(new Vector2(x, y));
  }

}
