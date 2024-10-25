package com.berray.objects;

import com.berray.Game;
import com.berray.GameObject;
import com.berray.math.Matrix4;
import com.berray.math.Vec3;
import com.raylib.Raylib;

import java.util.function.BiConsumer;

import static com.raylib.Raylib.BeginMode3D;
import static com.raylib.Raylib.Camera3D;

public class Root3D extends GameObject {

  @Override
  public void visitDrawChildren(BiConsumer<String, Runnable> visitor) {
    Camera3D camera = get("camera");
    visitor.accept(get("layer", Game.DEFAULT_LAYER), () -> BeginMode3D(camera));
    super.visitDrawChildren(visitor);
    visitor.accept(get("layer", Game.DEFAULT_LAYER), Raylib::EndMode3D);
  }

  @Override
  protected void ensureTransformCalculated() {
    GameObject parent = getParent();
    if (transformDirty || (parent != null && parent.isTransformDirty())) {
      setTransformDirty(); // be sure to notify children that the transform is recalculated
      Vec3 pos = getOrDefault("pos", Vec3.origin());
      Matrix4 rotationMatrix = getOrDefault("rotationMatrix", Matrix4.identity());
      Vec3 scale = getOrDefault("scale", new Vec3(1.0f, 1.0f, 1.0f));

      localTransform = Matrix4.identity()
          .multiply(Matrix4.fromTranslate(pos.getX(), pos.getY(), pos.getZ()))
          .multiply(rotationMatrix)
          .multiply(Matrix4.fromScale(scale.getX(), scale.getY(), scale.getZ()));

      Matrix4 parentsWorldTransform = parent == null ? Matrix4.identity() : parent.getWorldTransform();
      this.worldTransform = parentsWorldTransform.multiply(localTransform);

      worldTransform = parentsWorldTransform.multiply(localTransform);
      transformDirty = false;
    }
  }

}
