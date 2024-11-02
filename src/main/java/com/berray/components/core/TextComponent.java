package com.berray.components.core;

import com.berray.GameObject;
import com.berray.math.Color;
import com.berray.math.Vec2;

import static com.raylib.Jaylib.*;

/**
 * Component which displays some fixed text.
 */
public class TextComponent extends Component {
  private String text;
  private int fontHeight = 20;
  private int width;

  public TextComponent(String text) {
    super("text");
    setText(text);
  }

  @Override
  public void add(GameObject gameObject) {
    registerGetter("size", this::getSize);
    registerGetter("render", () -> true);
    registerBoundProperty("text", this::getText, this::setText);
    registerBoundProperty("fontHeight", this::getFontHeight, this::setFontHeight);
  }


  /**
   * Sets the text which should be drawn. Note that this may change the size of the object
   * and therefore force recalculation of the transformation matrix.
   *
   * @type property
   */
  public void setText(String text) {
    this.text = text;
    updateSize();
  }

  /**
   * Returns the text which should be drawn.
   *
   * @type property
   */
  public String getText() {
    return text;
  }

  /**
   * Returns the font height.
   *
   * @type property
   */
  public int getFontHeight() {
    return fontHeight;
  }

  /**
   * Sets the font height. Note that this may change the size of the object
   * and therefore force recalculation of the transformation matrix.
   *
   * @type property
   */
  public void setFontHeight(int fontHeight) {
    this.fontHeight = fontHeight;
    updateSize();
  }

  /**
   * Returns the size of the component.
   *
   * @type property
   */
  private Vec2 getSize() {
    return new Vec2(width, fontHeight);
  }

  private void updateSize() {
    int newWidth = MeasureText(text, fontHeight);
    if (newWidth != width && gameObject != null) {
      // when the width of the text changes, recalculate transform (as the component might
      // be moved when center or right aligned)
      gameObject.setTransformDirty();
    }
    this.width = newWidth;
  }

  @Override
  public void draw() {
    rlPushMatrix();
    {
      Color color = gameObject.getOrDefault("color", Color.BLACK);
      rlMultMatrixf(gameObject.getWorldTransform().toFloatTransposed());
      DrawText(text, 0, 0, fontHeight, color.toRaylibColor());
    }
    rlPopMatrix();
  }

  /** Creates a text component with fixed initial text. */
  public static TextComponent text(String text) {
    return new TextComponent(text);
  }
}
