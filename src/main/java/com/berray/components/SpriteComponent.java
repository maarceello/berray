package com.berray.components;

import com.berray.GameObject;
import static com.raylib.Jaylib.*;

public class SpriteComponent extends Component {
  private final Texture texture;
  private String state = "none";


  // Construct
  public SpriteComponent(Texture texture) {
    this.texture = texture;
  }

  // Black Magic
  public SpriteComponent anim(String animation) {
    this.state = animation;
    return this;
  }

  // Getter
  public Texture getTexture() {
    return texture;
  }

  @Override
  public void draw(GameObject gameObject) {

    PosComponent pos = gameObject.getComponent(PosComponent.class);

    if (pos == null) {
      return;
    }

    DrawTextureV(this.texture, pos.getPos(), WHITE);
  }
  // TODO: Load the Sprite not by the path rather the name of the asset for exmaple: "bean"
  // Static method to just call "sprite()"
  public static SpriteComponent sprite(String textureName) {
    return new SpriteComponent(LoadTexture(textureName));
  }

}
