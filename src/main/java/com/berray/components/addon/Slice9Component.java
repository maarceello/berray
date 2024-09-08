package com.berray.components.addon;

import com.berray.GameObject;
import com.berray.assets.AssetType;
import com.berray.components.core.Component;
import com.berray.math.Vec2;
import com.raylib.Jaylib;
import com.raylib.Raylib;

import static com.raylib.Jaylib.*;

public class Slice9Component extends Component {
  /**
   * Name of texture or sprite sheet asset.
   */
  public String textureName;
  /** Size of the component. */
  public Vec2 size;

  public Slice9Component(String texture) {
    super("slice9");
    this.textureName = texture;
  }

  @Override
  public void add(GameObject gameObject) {
    registerBoundProperty("size", this::getSize, this::setSize);
    registerGetter("render", () -> true);
  }

  public Vec2 getSize() {
    return size;
  }

  public void setSize(Vec2 size) {
    this.size = size;
  }

  @Override
  public void draw() {
    rlPushMatrix();
    {
      rlMultMatrixf(gameObject.getWorldTransformWithoutAnchor().toFloatTransposed());

      Texture texture = getAssetManager().getAsset(this.textureName, AssetType.SPRITE).getAsset();

      NPatchInfo nPatchInfo = new NPatchInfo()
          .top(8)
          .bottom(8)
          .left(8)
          .right(8)
          .layout(Raylib.NPATCH_NINE_PATCH)
          .source(new Jaylib.Rectangle(0,0,texture.width(), texture.height()));
      Raylib.DrawTextureNPatch(texture, nPatchInfo, new Jaylib.Rectangle(0,0,size.getX(), size.getY()), new Jaylib.Vector2(0,0), 0, WHITE);
    }
    rlPopMatrix();
  }

  public static Slice9Component slice9(String textureName) {
    return new Slice9Component(textureName);
  }


}
