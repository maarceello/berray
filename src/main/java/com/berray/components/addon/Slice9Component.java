package com.berray.components.addon;

import com.berray.GameObject;
import com.berray.assets.AssetType;
import com.berray.components.core.Component;
import com.berray.math.Color;
import com.berray.math.Vec2;
import com.raylib.Raylib;

public class Slice9Component extends Component {
  /**
   * Name of texture or sprite sheet asset.
   */
  public String textureName;
  /**
   * Size of the component.
   */
  public Vec2 size;
  private int top;
  private int bottom;
  private int left;
  private int right;

  public Slice9Component(String texture, Vec2 size, int top, int bottom, int left, int right) {
    super("slice9");
    this.textureName = texture;
    this.size = size;
    this.top = top;
    this.bottom = bottom;
    this.left = left;
    this.right = right;
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
    Raylib.Texture texture = getAssetManager().getAsset(this.textureName, AssetType.SPRITE).getAsset();

    Color color = gameObject.getOrDefault("color", Color.WHITE);

    Raylib.NPatchInfo nPatchInfo = new Raylib.NPatchInfo()
        .top(top)
        .bottom(bottom)
        .left(left)
        .right(right)
        .layout(Raylib.NPATCH_NINE_PATCH)
        .source(new Raylib.Rectangle().x(0).y(0).width(texture.width()).height(texture.height()));
    Raylib.DrawTextureNPatch(texture, nPatchInfo, new Raylib.Rectangle().x(0).y(0).width(size.getX()).height(size.getY()), new Raylib.Vector2().x(0).y(0), 0, color.toRaylibColor());
  }

  /**
   * Creates a new slice9 sprite
   *
   * @param textureName name of the texture
   * @param size        size in which to stretch the component
   * @param sizes       size from top, bottom, left and right of the corner components.
   */
  public static Slice9Component slice9(String textureName, Vec2 size, int sizes) {
    return new Slice9Component(textureName, size, sizes, sizes, sizes, sizes);
  }
}
