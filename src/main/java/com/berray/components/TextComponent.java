package com.berray.components;

import com.berray.GameObject;

import static com.raylib.Jaylib.DrawText;
import static com.raylib.Jaylib.BLACK;

public class TextComponent extends Component {
  private final String text;

  public TextComponent(String text) {
    super("text");
    this.text = text;
  }

  @Override
  public void draw(GameObject gameObject) {
    PosComponent pos = gameObject.getComponent(PosComponent.class);

    if (pos != null) {
      DrawText(text, (int) pos.getPos().x(), (int) pos.getPos().y(), 20, BLACK);
    } else {
      DrawText(text, 0, 0, 20, BLACK);
    }


  }

  public static TextComponent text(String text) {
    return new TextComponent(text);
  }
}
