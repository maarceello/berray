package com.berray.objects;

import com.berray.GameObject;
import com.berray.math.Matrix4;
import com.berray.math.Vec3;

import static com.raylib.Raylib.*;

public class Root3D extends GameObject {


  public Root3D() {
    registerPropertyGetter("render", () -> true);
  }

  @Override
  public void draw() {
    Camera3D camera = get("camera");
    if (camera != null) {
      BeginMode3D(camera);
      //DrawGrid(10, 1.0f);
      super.draw();
      EndMode3D();
    }
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
