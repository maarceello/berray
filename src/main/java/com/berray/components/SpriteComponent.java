package com.berray.components;

import com.berray.GameObject;
import com.berray.math.Rect;
import com.berray.math.Vec2;

import static com.berray.AssetManager.getSprite;
import static com.raylib.Jaylib.Rectangle;
import static com.raylib.Jaylib.Vector2;
import static com.raylib.Jaylib.Texture;
import static com.raylib.Jaylib.DrawTexturePro;
import static com.raylib.Jaylib.WHITE;

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

    PosComponent pos = gameObject.getComponent(PosComponent.class);
    RotateComponent rotate = gameObject.getComponent(RotateComponent.class);

    DrawTexturePro(
        this.texture,
        new Rectangle(0, 0, this.texture.width(), this.texture.height()),
        pos != null ? new Rectangle(pos.getPos().getX(), pos.getPos().getY(), texture.width(), texture.height()) : new Rectangle(0, 0, texture.width(), texture.height()),
        new Vector2((float) this.texture.width() / 2, (float) this.texture.height() / 2),
        rotate != null ? rotate.getAngle() : 0,
        WHITE);

  }

  @Override
  public void add(GameObject gameObject) {
    gameObject.registerGetter("localArea", this::localArea);
  }

  private Rect localArea() {
    Vec2 pos = gameObject.get("pos");
    if (pos == null) {
      return null;
    }
    int w = texture.width();
    int h = texture.height();
    return new Rect(pos.getX() - w / 2, pos.getY() - h / 2, w, h);
  }

  // Static method to just call "sprite()" get the sprite from the asset manager and put in into the texture for the sprite component
  public static SpriteComponent sprite(String name) {
    return new SpriteComponent(getSprite(name));
  }
}
