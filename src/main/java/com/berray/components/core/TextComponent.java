package com.berray.components.core;

import com.berray.GameObject;
import com.berray.math.Rect;
import com.berray.math.Vec2;
import com.raylib.Raylib;

import java.util.function.Consumer;

import static com.raylib.Jaylib.*;
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

  private Vec2 getSize() {
    return new Vec2(width, fontHeight);
  }

  @Override
  public void draw() {
    Raylib.rlPushMatrix();
    {
      Raylib.rlMultMatrixf(gameObject.getWorldTransform().toFloatTransposed());
      DrawText(text, 0,0, fontHeight, BLACK);
    }
    Raylib.rlPopMatrix();
  }

  @Override
  public void add(GameObject gameObject) {
    gameObject.registerGetter("localArea", this::localArea);
    gameObject.registerGetter("size", this::getSize);
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