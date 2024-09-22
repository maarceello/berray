package com.berray.components.core;

import com.berray.math.Matrix4;
import com.berray.math.Vec2;

public class PosComponent2d extends PosComponent<Vec2> {
  public PosComponent2d(Vec2 pos) {
    super(pos);
  }

  @Override
  protected Vec2 move(Vec2 pos, Vec2 amount) {
    return pos.move(amount);
  }

  @Override
  protected Vec2 move(Vec2 pos, Vec2 amount, float scale) {
    return pos.move(amount.scale(scale));
  }

  @Override
  protected Matrix4 getTransform() {
    return Matrix4.fromTranslate(pos.getX(), pos.getY(), 0);
  }

  public static PosComponent2d pos(float x, float y) {
    return new PosComponent2d(new Vec2(x, y));
  }

  public static PosComponent2d pos(Vec2 pos) {
    return new PosComponent2d(pos);
  }


}
