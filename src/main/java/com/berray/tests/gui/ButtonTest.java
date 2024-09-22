package com.berray.tests.gui;

import com.berray.BerrayApplication;
import com.berray.GameObject;
import com.berray.components.CoreComponentShortcuts;
import com.berray.components.core.AnchorType;
import com.berray.math.Color;
import com.berray.math.Vec2;

import static com.berray.objects.gui.Button.button;

public class ButtonTest extends BerrayApplication implements CoreComponentShortcuts {
  @Override
  public void game() {

    Vec2 center = center();
    GameObject root = add(
        rotate(0),
        pos(center)
    );

    for (AnchorType type : AnchorType.values()/* Arrays.asList(AnchorType.CENTER)*/) {
      int x = type.ordinal() % 3;
      int y = type.ordinal() / 3;
      boolean toggle = type.ordinal() % 2 == 1;

      GameObject neutral = makeButtonComponent(128, Vec2.origin(), "neutral");
      GameObject hover = makeButtonComponent(180, Vec2.origin(), "hover");
      GameObject armed = makeButtonComponent(180, new Vec2(5.0f, 5.0f), "armed");
      GameObject pressed = makeButtonComponent(180, new Vec2(3.0f, 3.0f), "pressed");

      root.add(
          button("testbutton", toggle)
              .neutral(neutral)
              .hover(hover)
              .armed(armed)
              .pressed(pressed),
          pos(20 + x * 470 - center.getX(), 20 + y * 470 - center.getY()),
          anchor(type),
          toggle ? "toggle" : "push"
      );
    }
  }

  private GameObject makeButtonComponent(int color, Vec2 pos, String tag) {
    GameObject buttonComponent = make(
        rect(300, 300),
        color(color, color, color),
        area(),
        pos(pos),
        anchor(AnchorType.TOP_LEFT),
        tag
    );

    buttonComponent.add(
        text(tag),
        pos(150, 150),
        color(Color.BLACK),
        anchor(AnchorType.CENTER)
    );

    return buttonComponent;
  }

  @Override
  public void initWindow() {
    width(1000);
    height(1000);
    background(Color.BLACK);
    title("Button Test");
  }

  public static void main(String[] args) {
    new ButtonTest().runGame();
  }
}
