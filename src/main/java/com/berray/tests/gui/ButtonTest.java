package com.berray.tests.gui;

import com.berray.BerrayApplication;
import com.berray.GameObject;
import com.berray.components.CoreComponentShortcuts;
import com.berray.components.core.AnchorType;
import com.berray.math.Vec2;
import com.raylib.Jaylib;

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

      root.add(
          button("testbutton")
              .neutral(
                  rect(300, 300),
                  color(128, 128, 128),
                  area(),
                  "neutral"
                  ,anchor(AnchorType.TOP_LEFT)
                  //, DebugComponent.debug()
              )
              .hover(
                  rect(300, 300),
                  color(180, 180, 180),
                  area(),
                  "hover"
                  ,anchor(AnchorType.TOP_LEFT)
              )
              .armed(
                  rect(300, 300),
                  color(180, 180, 180),
                  pos(5.0f, 5.0f),
                  area(),
                  "armed"
                  ,anchor(AnchorType.TOP_LEFT)
              ),
          pos(20 + x * 470 - center.getX(), 20 + y * 470 - center.getY()),
          anchor(type)
          //, DebugComponent.debug()
      );
    }

//    add(
//        Label.label(() -> "BB: " + button.getBoundingBox()),
//        color(255, 255, 255),
//        pos(0, 0),
//        anchor(AnchorType.TOP_LEFT)
//    );

  }

  @Override
  public void initWindow() {
    width(1000);
    height(1000);
    background(Jaylib.BLACK);
    title("Databinding Test");
  }

  public static void main(String[] args) {
    new ButtonTest().runGame();
  }
}
