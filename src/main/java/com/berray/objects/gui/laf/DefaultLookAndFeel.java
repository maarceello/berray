package com.berray.objects.gui.laf;

import com.berray.GameObject;
import com.berray.components.core.Component;
import com.berray.event.CoreEvents;
import com.berray.event.PropertyChangeEvent;
import com.berray.math.Color;
import com.berray.math.Insets;
import com.berray.math.Rect;
import com.berray.math.Vec2;
import com.berray.objects.gui.Button;
import com.berray.objects.gui.ButtonType;
import com.berray.objects.gui.Slider;
import com.raylib.Jaylib;

import static com.raylib.Raylib.*;

public class DefaultLookAndFeel implements LookAndFeelManager {
  private Color foregroundColor;
  private Color foregroundColorLight;
  private Color foregroundColorDark;
  private Color backgroundColor;
  private int borderThickness = 4;
  private int sliderKnobWidth = 20;
  private int sliderKnobGap = 2;

  public DefaultLookAndFeel() {
    setForegroundColor(Color.GOLD);
    setBackgroundColor(Color.BLACK);
  }

  public void setForegroundColor(Color foregroundColor) {
    this.foregroundColor = foregroundColor;
    this.foregroundColorLight = foregroundColor.brighter(0.7f);
    this.foregroundColorDark = foregroundColor.darker(0.7f);
  }

  public void setBackgroundColor(Color backgroundColor) {
    this.backgroundColor = backgroundColor;
  }

  @Override
  public void drawBorder(GameObject gameObject, String border) {
    if (border == null) {
      return;
    }

    Vec2 size = gameObject.get("size");
    Rect target = new Rect(Vec2.origin(), size);
    switch (border) {
      case "raised":
        drawBevelBorder(target, borderThickness, foregroundColorLight, foregroundColorDark);
        break;
      case "lowered":
        drawBevelBorder(target, borderThickness, foregroundColorDark, foregroundColorLight);
        break;
      case "bevel":
        drawBevelBorder(target, borderThickness / 3, foregroundColorLight, foregroundColorDark);
        drawBevelBorder(target.reduce(borderThickness / 3.0f), borderThickness - (2 * borderThickness / 3), foregroundColor, foregroundColor);
        drawBevelBorder(target.reduce((borderThickness / 3.0f) * 2), borderThickness / 3, foregroundColorDark, foregroundColorLight);
        break;
      case "emboss":
        drawBevelBorder(target, borderThickness / 3, foregroundColorDark, foregroundColorLight);
        drawBevelBorder(target.reduce(borderThickness / 3.0f), borderThickness - (2 * borderThickness / 3), foregroundColor, foregroundColor);
        drawBevelBorder(target.reduce((borderThickness / 3.0f) * 2), borderThickness / 3, foregroundColorLight, foregroundColorDark);
        break;
      case "solid":
      default:
        DrawRectangleLinesEx(new Jaylib.Rectangle(0, 0,
                (int) (size.getX()), (int) (size.getY())),
            borderThickness, foregroundColor.toRaylibColor());
    }

  }

  private void drawBevelBorder(Rect target, int thickness, Color topLeft, Color bottomRight) {
    for (int i = 0; i < thickness; i++) {
      // top
      DrawLine(
          (int) target.getX() + i, (int) target.getY() + i,
          (int) (target.getX() + target.getWidth() - i), (int) target.getY() + i,
          topLeft.toRaylibColor());

      // bottom
      DrawLine(
          (int) target.getX() + i, (int) (target.getY() + target.getHeight() - i),
          (int) (target.getX() + target.getWidth() - i), (int) (target.getY() + target.getHeight() - i),
          bottomRight.toRaylibColor());

      // left
      DrawLine(
          (int) target.getX() + i, (int) target.getY() + i,
          (int) target.getX() + i, (int) (target.getY() + target.getHeight()) - i,
          topLeft.toRaylibColor());

      // right
      DrawLine(
          (int) (target.getX() + target.getWidth() - i), (int) target.getY() + i,
          (int) (target.getX() + target.getWidth() - i), (int) (target.getY() + target.getHeight()) - i,
          bottomRight.toRaylibColor());
    }
  }

  @Override
  public Insets getBorderInsets(GameObject gameObject, String border) {
    if (border == null) {
      return Insets.NONE;
    }
    return new Insets(borderThickness, borderThickness, borderThickness, borderThickness);
  }

  @Override
  public void clearBackground(Button button) {
    DrawRectangleRec(button.getPaintArea().toRectangle(), foregroundColor.toRaylibColor());
  }

  @Override
  public void installToButton(Button button) {
    if (button.getButtonType() == ButtonType.CHECKBOX || button.getButtonType() == ButtonType.RADIO) {
      button.addComponents(new DrawCheckboxComponent(button.getButtonType() == ButtonType.RADIO));
      return;
    }


    button.set("border", "raised");
    button.on(CoreEvents.PROPERTY_CHANGED, (PropertyChangeEvent event) -> {
      if (event.getPropertyName().equals("armed") || event.getPropertyName().equals("pressed")) {
        boolean pressed = button.isPressed();
        boolean armed = button.isArmed();
        button.set("border", pressed || armed ? "lowered" : "raised");
      }
    });
  }

  @Override
  public void installToSlider(Slider slider) {
    slider.addComponents(new DrawSliderComponent());
  }

  public class DrawCheckboxComponent extends Component {

    private final boolean radio;

    public DrawCheckboxComponent(boolean radio) {
      super("drawCheckboxComponent");
      this.radio = radio;
    }

    @Override
    public void add(GameObject gameObject) {
      registerGetter("render", () -> true);
    }

    @Override
    public void draw() {
      super.draw();

      Vec2 size = gameObject.get("size");
      float minSize = Math.min(size.getX(), size.getY());
      Rect rect = new Rect(Vec2.origin(), new Vec2(minSize, minSize));
      DrawRectangleRec(rect.toRectangle(), foregroundColor.toRaylibColor());
      drawBevelBorder(rect, borderThickness, foregroundColorDark, foregroundColorLight);

      Rect crossSize = rect.reduce((float) borderThickness + sliderKnobGap);

      if (gameObject.get("pressed", false) == Boolean.TRUE) {
        if (radio) {
          drawBevelBorder(crossSize, borderThickness, foregroundColorLight, foregroundColorDark);
        } else {
          drawCross(crossSize, borderThickness, foregroundColorDark);
        }
      }
    }

  }

  private void drawCross(Rect crossSize, int thickness, Color color) {
    for (int i = 0; i < thickness; i++) {
      DrawLine(
          (int) crossSize.getX() + i, (int) crossSize.getY(),
          (int) (crossSize.getX() + crossSize.getWidth()), (int) (crossSize.getY() + crossSize.getHeight() - i),
          color.toRaylibColor());
      DrawLine(
          (int) crossSize.getX(), (int) crossSize.getY() + i,
          (int) (crossSize.getX() + crossSize.getWidth() - i), (int) (crossSize.getY() + crossSize.getHeight()),
          color.toRaylibColor());

      DrawLine(
          (int) (crossSize.getX() + crossSize.getWidth() - i), (int) crossSize.getY(),
          (int) (crossSize.getX()), (int) (crossSize.getY() + crossSize.getHeight() - i),
          color.toRaylibColor());

      DrawLine(
          (int) (crossSize.getX() + crossSize.getWidth()), (int) crossSize.getY() + i,
          (int) (crossSize.getX() + i), (int) (crossSize.getY() + crossSize.getHeight()),
          color.toRaylibColor());
    }

  }


  private class DrawSliderComponent extends Component {

    public DrawSliderComponent() {
      super("drawSliderComponent");
    }

    @Override
    public void add(GameObject gameObject) {
      registerGetter("render", () -> true);
    }

    @Override
    public void draw() {
      super.draw();

      Vec2 size = gameObject.get("size");
      Rect sliderRect = new Rect(Vec2.origin(), size);
      DrawRectangleRec(sliderRect.toRectangle(), foregroundColor.toRaylibColor());
      drawBevelBorder(sliderRect, borderThickness, foregroundColorDark, foregroundColorLight);

      Slider slider = (Slider) gameObject;
      int min = slider.getMin();
      int max = slider.getMax();
      int value = slider.getValue();
      if (max - min == 0) {
        return;
      }
      float percent = (float) value / (max - min);

      // calculate center and size of knob
      float innerWidth = size.getX() - borderThickness * 2 - sliderKnobWidth - sliderKnobGap * 2;
      float innerInsets = borderThickness + sliderKnobGap + sliderKnobWidth / 2.0f;
      Vec2 knobPos = sliderRect.getPos().add(new Vec2(innerWidth * percent + innerInsets, size.getY() / 2.0f));
      Vec2 knobSize = new Vec2(sliderKnobWidth, size.getY() - borderThickness * 2 - sliderKnobGap);

      Rect knobRectangle = new Rect(knobPos.sub(knobSize.scale(0.5f)), knobSize);
      drawBevelBorder(knobRectangle, borderThickness, foregroundColorLight, foregroundColorDark);


    }
  }
}
