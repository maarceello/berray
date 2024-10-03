package com.berray.components.core;

import com.berray.math.Matrix4;
import com.berray.math.Vec2;
import com.berray.math.Vec3;

public class PosComponent3d extends PosComponent<Vec3> {
  public PosComponent3d(Vec3 pos) {
    super(pos);
  }

  @Override
  protected Vec3 move(Vec3 pos, Vec3 amount) {
    return pos.move(amount);
  }

  @Override
  protected Vec3 move(Vec3 pos, Vec3 amount, float scale) {
    return pos.move(amount.scale(scale));
  }

  @Override
  protected Matrix4 getTransform() {
    return Matrix4.fromTranslate(pos.getX(), pos.getY(), pos.getZ());
  }

  public static PosComponent3d pos(float x, float y, float z) {
    return new PosComponent3d(new Vec3(x, y, z));
  }

  public static PosComponent3d pos(Vec3 pos) {
    return new PosComponent3d(pos);
  }


}
