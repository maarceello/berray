package com.berray.components;

import com.berray.GameObject;
import com.berray.math.Rect;
import com.berray.math.Vec2;

import static com.raylib.Jaylib.DrawText;
import static com.raylib.Jaylib.BLACK;
import static com.raylib.Raylib.MeasureText;

public class TextComponent extends Component {
  private final String text;
  private int fontHeight = 20;

  public TextComponent(String text) {
    super("text");
    this.text = text;
  }

  @Override
  public void draw() {
    PosComponent pos = gameObject.getComponent(PosComponent.class);

    if (pos != null) {
      DrawText(text, (int) pos.getPos().getX(), (int) pos.getPos().getY(), fontHeight, BLACK);
    } else {
      DrawText(text, 0, 0, fontHeight, BLACK);
    }
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
    int width = MeasureText(text, fontHeight);
    return new Rect(pos.getX() , pos.getY() , width, fontHeight);
  }


  public static TextComponent text(String text) {
    return new TextComponent(text);
  }
}
