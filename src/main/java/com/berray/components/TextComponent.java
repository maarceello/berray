package com.berray.components;

import com.berray.GameObject;
import com.berray.math.Rect;
import com.berray.math.Vec2;

import java.util.function.Consumer;

import static com.raylib.Jaylib.DrawText;
import static com.raylib.Jaylib.BLACK;
import static com.raylib.Raylib.MeasureText;

public class TextComponent extends Component {
  private String text;
  private int fontHeight = 20;
  private int width;

  public TextComponent(String text) {
    super("text");
    setText(text);
  }

  public void setText(String text) {
    this.width = MeasureText(text, fontHeight);
    this.text = text;
  }

  @Override
  public void draw() {
    Vec2 pos = gameObject.getOrDefault("pos", Vec2.origin());
    AnchorType anchor = gameObject.getOrDefault("anchor", AnchorType.CENTER);

    int w2 = width / 2;
    int h2 = fontHeight / 2;
    float anchorX = w2 + anchor.getX() * w2;
    float anchorY = h2 + anchor.getY() * h2;

    DrawText(text, (int) (pos.getX() - anchorX), (int) (pos.getY() - anchorY), fontHeight, BLACK);
  }

  @Override
  public void add(GameObject gameObject) {
    gameObject.registerGetter("localArea", this::localArea);
    gameObject.registerSetter("text", (Consumer<String>) this::setText);
  }

  private Rect localArea() {
    Vec2 pos = gameObject.get("pos");
    if (pos == null) {
      return null;
    }
    int width = MeasureText(text, fontHeight);
    return new Rect(pos.getX(), pos.getY(), width, fontHeight);
  }


  public static TextComponent text(String text) {
    return new TextComponent(text);
  }
}
