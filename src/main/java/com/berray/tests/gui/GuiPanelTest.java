package com.berray.tests.gui;

import com.berray.BerrayApplication;
import com.berray.GameObject;
import com.berray.components.CoreComponentShortcuts;
import com.berray.components.core.AnchorType;
import com.berray.math.Color;
import com.berray.math.Vec2;
import com.berray.objects.gui.EventListenerCapable;
import com.berray.objects.gui.ReflectionDomainObject;

import static com.berray.objects.gui.panel.PanelBuilder.makePanel;
import static com.berray.objects.gui.panel.RowBuilder.makeRow;


public class GuiPanelTest extends BerrayApplication implements CoreComponentShortcuts {

  @Override
  public void initWindow() {
    width(1000);
    height(512);
    background(Color.GRAY);
    title("Gui Panel Test");
  }

  @Override
  public void game() {
    background(Color.BLACK);

    EventListenerCapable object = new ReflectionDomainObject(new Player(1.0f, 10.0f));
    GameObject playerImage = add(
        circle(10),
        pos(0, 0),
        anchor(AnchorType.CENTER),
        color(Color.GOLD)
    );

    GameObject panel = add(
        makePanel()
            .columnWidths(50.0f, 100.0f, 200.0f)
            .bind(object)
            .color(Color.BLACK, Color.GOLD)
            .frame(5, Color.WHITE)
            .row(makeRow(20)
                .colSpan(3)
                .align(AnchorType.TOP)
                .background(Color.GOLD)
                .label("Panel Title", Color.BLACK)
            )
            .row(makeRow(20)
                .label("X: ")
                .align(AnchorType.TOP_RIGHT).label("${pos.x} px")
                .slider("x", 0, width())
            )
            .row(makeRow(20)
                .label("Y: ")
                .align(AnchorType.TOP_RIGHT).label("${pos.y} px")
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

  /**
   * Mutable version of the {@link Vec2} so it can be used in data binding.
   */
  public static class MutableVec2 {
    private float x;
    private float y;

    public MutableVec2() {
    }

    public MutableVec2(float x, float y) {
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

  public static void main(String[] args) {
    new GuiPanelTest().runGame();
  }

  public static class Player {
    private float x;
    private float y;
    private MutableVec2 pos;

    public Player(float x, float y) {
      this.x = x;
      this.y = y;
      this.pos = new MutableVec2(x, y);
    }

    public float getX() {
      return x;
    }

    public void setX(float x) {
      this.pos.setX(x);
      this.x = x;
    }

    public float getY() {
      return y;
    }

    public void setY(float y) {
      this.pos.setY(y);
      this.y = y;
    }

    public MutableVec2 getPos() {
      return pos;
    }

    public void setPos(MutableVec2 pos) {
      this.pos = pos;
    }
  }


}
