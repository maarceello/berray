package com.berray.tests;

import com.berray.BerrayApplication;
import com.berray.components.CoreComponentShortcuts;
import com.berray.math.Color;
import com.berray.math.Vec2;

import static com.berray.components.core.AnimateComponent.animate;
import static com.berray.components.core.AnimateComponent.animateVec2;

public class AnimateTest extends BerrayApplication implements CoreComponentShortcuts {
  @Override
  public void game() {
    addFpsLabel();
    addTimingsLabel();
    add(
        rect(100, 100),
        pos(center()),
        color(Color.GOLD),
        animateVec2("pos")
            .loop()
            .keyFrame(0.0f, new Vec2(100, 100))
            .keyFrame(2.0f, new Vec2(100, 900))
            .keyFrame(4.0f, new Vec2(900, 900))
            .keyFrame(6.0f, new Vec2(900, 100))
            .keyFrame(8.0f, new Vec2(100, 100)),
        animate("color", Color::linearInterpolate)
            .loop()
            .keyFrame(0.0f, Color.GOLD)
            .keyFrame(1.0f, Color.GRAY)
            .keyFrame(4.0f, Color.GREEN)
            .keyFrame(5.0f, Color.WHITE)
            .keyFrame(8.0f, Color.RED)
    );

    add(
        circle(20),
        pos(center()),
        color(Color.GOLD),
        animateVec2("pos")
            .loop()
            .keyFrame(0.0f, new Vec2(100, 300))
            .keyFrame(2.0f, new Vec2(900, 300))
    );

    add(
        circle(20),
        pos(center()),
        color(Color.GOLD),
        animateVec2("pos")
            .loop()
            .pingPong()
            .keyFrame(0.0f, new Vec2(100, 600))
            .keyFrame(2.0f, new Vec2(900, 600))
    );

  }

  @Override
  public void initWindow() {
    width(1024);
    height(1024);
    background(Color.GRAY);
    title("Animate Test");
  }

  public static void main(String[] args) {
    new AnimateTest().runGame();
  }
}
