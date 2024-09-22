package com.berray.tests.gui;

import com.berray.BerrayApplication;
import com.berray.GameObject;
import com.berray.assets.CoreAssetShortcuts;
import com.berray.components.CoreComponentShortcuts;
import com.berray.components.core.AnchorType;
import com.berray.math.Color;
import com.berray.math.Vec2;

import static com.berray.objects.gui.Button.pushButton;
import static com.berray.objects.gui.Button.toggleButton;

public class SpriteButtonTest extends BerrayApplication implements CoreComponentShortcuts, CoreAssetShortcuts {


  @Override
  public void game() {

    loadSprite("/ui/border", "resources/kenny-ui-pack/button_square_depth_border.png");
    loadSprite("/ui/checkbox/neutral", "resources/kenny-ui-pack/check_square_grey.png");
    loadSprite("/ui/checkbox/checked", "resources/kenny-ui-pack/check_square_grey_checkmark.png");

    Vec2 center = center();
    add(
        pushButton("pushbutton")
            .slice9("/ui/border", "Button", new Vec2(200, 50), 16, Color.WHITE, Color.GOLD),
        pos(center.getX(), center().getY() - 30),
        anchor(AnchorType.CENTER)
    );

    add(
        toggleButton("togglebutton")
            .slice9("/ui/border", "Togggle Button", new Vec2(250, 50), 16, Color.WHITE, Color.GRAY),
        pos(center.getX(), center().getY() + 30),
        anchor(AnchorType.CENTER)
    );

    add(
        toggleButton("checkbox")
            .neutral(makeSpriteComponent("/ui/checkbox/neutral", "text", Color.WHITE ))
            .armed(makeSpriteComponent("/ui/checkbox/checked", "text", Color.GRAY ))
            .pressed(makeSpriteComponent("/ui/checkbox/checked", "text", Color.WHITE ))
        ,
        pos(center.getX(), center().getY() + 80),
        anchor(AnchorType.CENTER)
    );
  }

  private GameObject makeSpriteComponent(String assetName, String text, Color color) {
    GameObject button = make(
        sprite(assetName),
        area(),
        pos(Vec2.origin()),
        anchor(AnchorType.TOP_LEFT),
        color(color)
    );

    button.add(
        text(text),
        area(),
        pos(40, 4),
        anchor(AnchorType.TOP_LEFT),
        color(Color.WHITE)
    );

    return button;
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
