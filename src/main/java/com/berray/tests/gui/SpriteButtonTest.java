package com.berray.tests.gui;

import com.berray.BerrayApplication;
import com.berray.GameObject;
import com.berray.assets.CoreAssetShortcuts;
import com.berray.components.CoreComponentShortcuts;
import com.berray.components.addon.Slice9Component;
import com.berray.components.core.AnchorType;
import com.berray.math.Color;
import com.berray.math.Vec2;

import static com.berray.GameObject.make;
import static com.berray.objects.gui.Button.pushButton;
import static com.berray.objects.gui.Button.toggleButton;

public class SpriteButtonTest extends BerrayApplication implements CoreComponentShortcuts, CoreAssetShortcuts {


  @Override
  public void game() {

    loadSprite("/ui/border", "resources/kenny-fantasy-ui-borders/Panel/panel-005.png");

    Vec2 center = center();
    add(
        pushButton("testbutton")
            .slice9("/ui/border", "Button", new Vec2(200, 50), 16, Color.WHITE, Color.GOLD),
        pos(center.getX(), center().getY() - 30),
        anchor(AnchorType.CENTER)
    );

    add(
        toggleButton("testbutton")
            .slice9("/ui/border", "Togggle Button", new Vec2(250, 50), 16, Color.WHITE, Color.GRAY),
        pos(center.getX(), center().getY() + 30),
        anchor(AnchorType.CENTER)
    );
  }

  @Override
  public void initWindow() {
    width(1000);
    height(500);
    background(Color.BLACK);
    title("Button Test");
  }

  public static void main(String[] args) {
    new SpriteButtonTest().runGame();
  }
}
