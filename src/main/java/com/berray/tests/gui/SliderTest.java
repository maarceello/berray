package com.berray.tests.gui;

import com.berray.BerrayApplication;
import com.berray.GameObject;
import com.berray.assets.CoreAssetShortcuts;
import com.berray.components.CoreComponentShortcuts;
import com.berray.components.core.AnchorType;
import com.berray.event.CoreEvents;
import com.berray.event.PropertyChangeEvent;
import com.berray.math.Color;
import com.berray.math.Vec2;
import com.berray.objects.guiold.Slider;


public class SliderTest extends BerrayApplication implements CoreComponentShortcuts, CoreAssetShortcuts {


  @Override
  public void game() {
    Vec2 center = center();

    loadSprite("/ui/slider/leftborder", "resources/kenny-ui-pack/space/bar_square_large_l_blue.png");
    loadSprite("/ui/slider/leftbar", "resources/kenny-ui-pack/space/bar_square_large_m_blue.png");
    loadSprite("/ui/slider/rightbar", "resources/kenny-ui-pack/space/bar_square_large_m_gray.png");
    loadSprite("/ui/slider/rightborder", "resources/kenny-ui-pack/space/bar_square_large_r_gray.png");

    GameObject leftBorder = GameObject.makeGameObject(
        sprite("/ui/slider/leftborder"),
        pos(0,0),
        anchor(AnchorType.CENTER)
    );
    GameObject rightBorder = GameObject.makeGameObject(
        sprite("/ui/slider/rightborder"),
        pos(0,0),
        anchor(AnchorType.CENTER)
    );
    GameObject rightBar = GameObject.makeGameObject(
        sprite("/ui/slider/rightbar"),
        pos(0,0),
        anchor(AnchorType.CENTER)
    );
    GameObject leftBar = GameObject.makeGameObject(
        sprite("/ui/slider/leftbar"),
        pos(0,0),
        anchor(AnchorType.CENTER)
    );

    Slider slider = add(
        new Slider(new Vec2(500, 24), 0, 100, 20)
            .leftBorder(leftBorder)
            .rightBorder(rightBorder)
            .leftBar(leftBar)
            .rightBar(rightBar),
        pos(center.getX(), center().getY()),
        anchor(AnchorType.CENTER)
    );

    GameObject text = add(
        text("foo"),
        pos(10,10),
        anchor(AnchorType.TOP_LEFT),
        color(Color.WHITE)
    );

    slider.on(CoreEvents.PROPERTY_CHANGED, (PropertyChangeEvent event) -> {
      if (event.getPropertyName().equals("value")) {
        text.set("text", String.format("Value: %.2f", event.<Float>getNewValue()));
      }
    });

  }


  @Override
  public void initWindow() {
    width(1000);
    height(500);
    background(Color.BLACK);
    title("Slider Test");
  }

  public static void main(String[] args) {
    new SliderTest().runGame();
  }
}
