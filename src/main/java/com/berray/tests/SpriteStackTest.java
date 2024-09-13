package com.berray.tests;

import com.berray.BerrayApplication;
import com.berray.GameObject;
import com.berray.assets.CoreAssetShortcuts;
import com.berray.components.CoreComponentShortcuts;
import com.berray.components.core.AnchorType;
import com.berray.math.Color;
import com.berray.math.Vec2;
import com.berray.objects.gui.Slider;

import static com.berray.assets.SpriteSheet.spriteSheet;
import static com.berray.components.incubator.SpriteStackComponent.spriteStack;
import static com.berray.objects.gui.Slider.slider;

public class SpriteStackTest extends BerrayApplication implements CoreComponentShortcuts, CoreAssetShortcuts {
  @Override
  public void game() {
    loadSpriteSheet("/stacks/deer", "resources/deer.png",
        spriteSheet().sliceY(22));

    GameObject root = add(
        scale(10),
        pos(center()),
        anchor(AnchorType.CENTER)
    );

    GameObject deer = root.add(
        spriteStack("/stacks/deer"),
        pos(0, 10),
        anchor(AnchorType.CENTER),
        "stack"
    );

    onUpdate("stack", (event) -> {
      GameObject stack = event.getParameter(0);
      float frameTime = event.getParameter(1);
      Float angle = stack.get("angle");
      angle += frameTime * 45f;
      stack.set("angle", angle);
    });

    Slider slider = root.add(
        slider(new Vec2(40, 5), 0, 180, 0),
        pos(0, 20),
        anchor(AnchorType.CENTER)
    );

    slider.on("propertyChange", event -> {
      float newValue = event.getParameter(2);
      deer.set("cameraAngle", newValue - 90);
    });


  }

  @Override
  public void initWindow() {
    width(600);
    height(600);
    background(Color.GRAY);
    title("Sprite Stacking Test");
  }

  public static void main(String[] args) {
    new SpriteStackTest().runGame();
  }
}
