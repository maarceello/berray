package com.berray.components.core;

import com.berray.GameObject;

import com.berray.math.Vec2;

import static com.berray.AssetManager.getSprite;
import static com.raylib.Jaylib.Texture;
import static com.raylib.Jaylib.WHITE;
import static com.raylib.Raylib.*;

public class SpriteComponent extends Component {
  public Texture texture;
  private String anim = "none";

  // Construct
  public SpriteComponent(Texture texture) {
    super("sprite");
    this.texture = texture;
  }

  // Black Magic
  public SpriteComponent anim(String animation) {
    this.anim = animation;
    return this;
  }

  @Override
  public void draw() {
    rlPushMatrix();
    {
      rlMultMatrixf(gameObject.getWorldTransform().toFloatTransposed());
      DrawTexture(this.texture, 0, 0, WHITE);
    }
    rlPopMatrix();
  }

  @Override
  public void add(GameObject gameObject) {
    gameObject.registerGetter("size", this::getSize);
  }

  private Vec2 getSize() {
    return new Vec2(texture.width(), texture.height());
  }

  // Static method to just call "sprite()" get the sprite from the asset manager and put in into the texture for the sprite component
  public static SpriteComponent sprite(String name) {
    return new SpriteComponent(getSprite(name));
  }
}
