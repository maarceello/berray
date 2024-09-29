package com.berray.tests.gui;

import com.berray.BerrayApplication;
import com.berray.GameObject;
import com.berray.components.CoreComponentShortcuts;
import com.berray.components.core.AnchorType;
import com.berray.math.Color;
import com.berray.math.Vec2;
import com.berray.objects.gui.EventListenerCapable;
import com.berray.objects.gui.Panel;
import com.berray.objects.gui.ReflectionDomainObject;


public class GuiPanelTest extends BerrayApplication implements CoreComponentShortcuts {

  @Override
  public void initWindow() {
    width(1500);
    height(768);
    background(Color.GRAY);
    title("Gui Panel Test");
  }

  @Override
  public void game() {
    background(Color.BLACK);

    EventListenerCapable object = new ReflectionDomainObject(new Player(1.0f, 10.0f));
    GameObject playerImage = add(
        circle(10),
        pos(0,0),
        anchor(AnchorType.CENTER),
        color(Color.GOLD)
    );

    GameObject panel = add(
        new Panel.PanelBuilder(50.0f, 100.0f, 200.0f)
            .bind(object)
            .color(Color.BLACK, Color.GOLD)
            .frame(5, Color.WHITE)
            .row(20, new Panel.RowBuilder()
                .colSpan(3)
                .align(AnchorType.TOP)
                .background(Color.GOLD)
                .label("Panel Title", Color.BLACK)
            )
            .row(20, new Panel.RowBuilder()
                .label("X: ")
                .align(AnchorType.TOP_RIGHT).label("${x} px")
                .slider("x", 0, width())
            )
            .row(20, new Panel.RowBuilder()
                .label("Y: ")
                .align(AnchorType.TOP_RIGHT).label("${y} px")
                .slider("y", 0, height())
            )
            .buildGameObject()
    );
    panel.set("anchor", AnchorType.CENTER);
    panel.set("pos", center());

    object.onPropertyChange(event -> {
      playerImage.set("pos", new Vec2(object.getProperty("x"), object.getProperty("y")));
    });
  }

  public static void main(String[] args) {
    new GuiPanelTest().runGame();
  }

  public static class Player {
    private float x;
    private float y;

    public Player(float x, float y) {
      this.x = x;
      this.y = y;
    }

    public float getX() {
      return x;
    }

    public void setX(float x) {
      this.x = x;
    }

    public float getY() {
      return y;
    }

    public void setY(float y) {
      this.y = y;
    }
  }


}
