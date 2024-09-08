package com.berray.tests.gui;

import com.berray.BerrayApplication;
import com.berray.GameObject;
import com.berray.assets.CoreAssetShortcuts;
import com.berray.components.CoreComponentShortcuts;
import com.berray.components.core.AnchorType;
import com.berray.math.Vec2;
import com.berray.objects.gui.Button;
import com.raylib.Jaylib;

import static com.berray.GameObject.make;
import static com.berray.objects.gui.Button.button;
import static com.berray.objects.gui.Button.pushButton;

public class SpriteButtonTest extends BerrayApplication implements CoreComponentShortcuts, CoreAssetShortcuts {


  @Override
  public void game() {

    loadSprite("/ui/border", "resources/kenny-fantasy-ui-borders/Border/panel-border-005.png");

    add(
        pushButton("testbutton")
            .neutral(makeButtonComponent(128, Vec2.origin(), "neutral")),

        pos(center()),
        anchor(AnchorType.CENTER)
    );
  }

  private GameObject makeButtonComponent(int color, Vec2 pos, String tag) {
    return make(
        rect(300, 300),
        color(color, color, color),
        area(),
        pos(pos),
        anchor(AnchorType.TOP_LEFT),
        tag
    );
  }


  @Override
  public void initWindow() {
    width(1000);
    height(1000);
    background(Jaylib.BLACK);
    title("Button Test");
  }

  public static void main(String[] args) {
    new SpriteButtonTest().runGame();
  }
}
