package com.berray.components;

import com.berray.GameObject;

import static com.raylib.Jaylib.DrawTexturePro;
import static com.raylib.Jaylib.Rectangle;
import static com.raylib.Jaylib.Texture;
import static com.raylib.Jaylib.Vector2;
import static com.raylib.Jaylib.WHITE;
import static com.berray.AssetManager.getSprite;

public class SpriteComponent extends Component {
  public  Texture texture;
  private String anim = "none";

  // Construct
  public SpriteComponent(Texture texture) {
    this.texture = texture;
  }

  // Black Magic
  public SpriteComponent anim(String animation) {
    this.anim = animation;
    return this;
  }

  @Override
  public void draw(GameObject gameObject) {

    PosComponent pos = gameObject.getComponent(PosComponent.class);
    RotateComponent rotate = gameObject.getComponent(RotateComponent.class);

    if (pos == null) {
      return;
    }

    DrawTexturePro(
        this.texture,
        new Rectangle(0, 0, this.texture.width(), this.texture.height()),
        new Rectangle(pos.getPos().x(), pos.getPos().y(), texture.width(), texture.height()),
        new Vector2((float) this.texture.width() / 2, (float) this.texture.height() / 2),
        rotate != null ? rotate.getAngle() : 0,
        WHITE);

  }
  // Static method to just call "sprite()" get the sprite from the asset manager and put in into the texture for the sprite component
  public static SpriteComponent sprite(String name) {
    return new SpriteComponent(getSprite(name));
  }

}
