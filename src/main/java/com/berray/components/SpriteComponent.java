package com.berray.components;

import com.berray.GameObject;
import com.berray.math.Rect;
import com.berray.math.Vec2;

import static com.berray.AssetManager.getSprite;
import static com.raylib.Jaylib.Vector2;
import static com.raylib.Jaylib.Texture;
import static com.raylib.Jaylib.DrawTextureEx;
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
    Vec2 pos = gameObject.getOrDefault("pos", Vec2.origin());
    Float angle = gameObject.getOrDefault("angle", 0f);
    AnchorType anchor = gameObject.getOrDefault("anchor", AnchorType.CENTER);

    float w2 = texture.width() / 2.0f;
    float h2 = texture.height() / 2.0f;

    float anchorX = w2 + anchor.getX() * w2;
    float anchorY = h2 + anchor.getY() * h2;

    DrawTextureEx(
        this.texture,
        new Vector2(pos.getX() - anchorX, pos.getY() - anchorY),
        angle,
        1.0f,
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
