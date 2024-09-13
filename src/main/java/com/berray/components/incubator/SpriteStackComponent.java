package com.berray.components.incubator;

import com.berray.GameObject;
import com.berray.assets.SpriteSheet;
import com.berray.components.core.Component;
import com.berray.math.Color;
import com.berray.math.Rect;
import com.berray.math.Vec2;
import com.raylib.Jaylib.Rectangle;

import static com.raylib.Raylib.*;

public class SpriteStackComponent extends Component {
  private final String assetName;
  private float angle = 0;
  private float cameraAngle = 0;

  public SpriteStackComponent(String assetName) {
    super("sprite-stack", "pos");
    this.assetName = assetName;
  }

  @Override
  public void add(GameObject gameObject) {
    registerBoundProperty("angle", this::getAngle, this::setAngle);
    registerBoundProperty("cameraAngle", this::getCameraAngle, this::setCameraAngle);
    registerGetter("size", () -> new Vec2(10, 6));
    registerGetter("render", () -> true);
  }

  @Override
  public void draw() {
    rlPushMatrix();
    {
      Color color = gameObject.getOrDefault("color", Color.WHITE);
      rlMultMatrixf(gameObject.getWorldTransform().toFloatTransposed());

      float _ang_dcos = (float) Math.cos(Math.toRadians(cameraAngle));
      float _ang_dsin = (float) -Math.sin(Math.toRadians(cameraAngle));


      SpriteSheet spriteSheet = getAssetManager().getAsset(assetName).getAsset();
      for (int frame = 0; frame < spriteSheet.getNumFrames(); frame++) {
        float dist = frame /*+ _z*/;
        float lx = dist * _ang_dcos;
        float ly = dist * _ang_dsin;

        Rect frameRect = spriteSheet.getFrame(spriteSheet.getNumFrames() - frame - 1);
        Vec2 rotateCenter = new Vec2(frameRect.getWidth() / 2.0f, frameRect.getHeight() / 2.0f);

        DrawTexturePro(spriteSheet.getTexture(), frameRect.toRectangle(),
            new Rectangle(0, -ly, frameRect.getWidth(), frameRect.getHeight()),
            rotateCenter.toVector2(),
            angle, color.toRaylibColor());
      }

    }
    rlPopMatrix();
  }

  public float getAngle() {
    return angle;
  }

  public void setAngle(float angle) {
    this.angle = angle;
  }

  public float getCameraAngle() {
    return cameraAngle;
  }

  public void setCameraAngle(float cameraAngle) {
    this.cameraAngle = cameraAngle;
  }

  public static SpriteStackComponent spriteStack(String assetName) {
    return new SpriteStackComponent(assetName);
  }
}
