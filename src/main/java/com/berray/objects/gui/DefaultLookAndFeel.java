package com.berray.objects.gui;

import com.berray.GameObject;
import com.berray.event.CoreEvents;
import com.berray.event.PropertyChangeEvent;
import com.berray.math.Color;
import com.berray.math.Insets;
import com.berray.math.Rect;
import com.berray.math.Vec2;
import com.berray.objects.gui.layout.NopLayoutManager;
import com.raylib.Jaylib;

import static com.raylib.Raylib.*;

public class DefaultLookAndFeel implements LookAndFeelManager {
  private Color foregroundColor;
  private Color foregroundColorLight;
  private Color foregroundColorDark;
  private Color backgroundColor;
  private int borderThickness = 10;

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
        drawBevelBorder(target, borderThickness,foregroundColorDark, foregroundColorLight);
        break;
      case "bevel":
        drawBevelBorder(target, borderThickness / 3, foregroundColorLight, foregroundColorDark);
        drawBevelBorder(target.reduce(borderThickness / 3), borderThickness - (2 * borderThickness / 3), foregroundColor, foregroundColor);
        drawBevelBorder(target.reduce((borderThickness / 3) * 2), borderThickness / 3, foregroundColorDark, foregroundColorLight);
        break;
      case "emboss":
        drawBevelBorder(target, borderThickness / 3, foregroundColorDark, foregroundColorLight);
        drawBevelBorder(target.reduce(borderThickness / 3), borderThickness - (2 * borderThickness / 3), foregroundColor, foregroundColor);
        drawBevelBorder(target.reduce((borderThickness / 3) * 2), borderThickness / 3, foregroundColorLight, foregroundColorDark);
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
  public void installToButton(Button button) {
    button.setBorder("raised");
    button.setLayoutManager(new NopLayoutManager());
    button.on(CoreEvents.PROPERTY_CHANGED, (PropertyChangeEvent event) -> {
      if (event.getPropertyName().equals("armed") || event.getPropertyName().equals("pressed")) {
        boolean pressed = button.isPressed();
        boolean armed = button.isArmed();
        button.setBorder(pressed || armed ? "lowered" : "raised");
      }
    });
  }

  @Override
  public void clearBackground(Button button) {
    DrawRectangleRec(button.getPaintArea().toRectangle(), foregroundColor.toRaylibColor());
  }
}
