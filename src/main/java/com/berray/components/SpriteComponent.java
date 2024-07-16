package com.berray.components;

import static com.raylib.Jaylib.*;

public class SpriteComponent extends Component {
  private final Texture texture;

  public SpriteComponent(Texture texture) {
    super(2);
    this.texture = texture;
  }

  public static Component sprite(String textureName) {
    return new SpriteComponent(LoadTexture(textureName));
  }

  public Texture getTexture() {
    return texture;
  }
}
