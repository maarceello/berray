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
            .neutral(makeButtonComponent("/ui/border", Vec2.origin(), "Button", Color.WHITE))
            .hover(makeButtonComponent("/ui/border", Vec2.origin(), "Button", new Color(1.0f, 0.8f, 0.0f)))
            .armed(makeButtonComponent("/ui/border", new Vec2(5,5), "Button", new Color(1.0f, 0.8f, 0.0f)))
            .pressed(makeButtonComponent("/ui/border", new Vec2(2,2), "Button", new Color(1.0f, 0.8f, 0.0f))),
        pos(center.getX(), center().getY() - 30),
        anchor(AnchorType.CENTER)
    );

    add(
        toggleButton("testbutton")
            .neutral(makeButtonComponent("/ui/border", Vec2.origin(), "Button", Color.WHITE))
            .hover(makeButtonComponent("/ui/border", Vec2.origin(), "Button", new Color(1.0f, 0.8f, 0.0f)))
            .armed(makeButtonComponent("/ui/border", new Vec2(5,5), "Button", new Color(1.0f, 0.8f, 0.0f)))
            .pressed(makeButtonComponent("/ui/border", new Vec2(2,2), "Button", new Color(1.0f, 0.8f, 0.0f))),
        pos(center.getX(), center().getY() + 30),
        anchor(AnchorType.CENTER)
    );
  }

  private GameObject makeButtonComponent(String assetName, Vec2 pos, String text, Color color) {
    GameObject button = make(
        Slice9Component.slice9(assetName, new Vec2(200, 50), 16),
        area(),
        pos(pos),
        anchor(AnchorType.TOP_LEFT),
        color(color)
    );

    button.add(
        text(text),
        pos(100, 25),
        anchor(AnchorType.CENTER)
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
