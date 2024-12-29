package com.berray.objects.gui;

import com.berray.BerrayApplication;
import com.berray.components.CoreComponentShortcuts;
import com.berray.components.core.AnchorType;
import com.berray.math.Color;
import com.berray.math.Vec2;
import com.berray.objects.gui.laf.DefaultLookAndFeel;
import com.berray.objects.gui.layout.NopLayoutManager;

public class GuiTest extends BerrayApplication implements CoreComponentShortcuts {
  @Override
  public void initWindow() {
    width(1000);
    height(1000);
    background(Color.GRAY);
    title("Gui Panel Test");
  }

  @Override
  public void game() {
    game.setDefaultLookAndFeelManager(new DefaultLookAndFeel());
    Frame frame = add(
        new Frame(new Vec2(400, 400), new NopLayoutManager()),
        pos(200, 500),
        anchor(AnchorType.CENTER),
        "frame"
    );

    frame.set("border", "raised");

    Button button = frame.add(
        new Button("test", new Vec2(300, 100)),
        pos(0, 0),
        "button"
    );

    button.add(
        new Label("Type: ${type}"),
        pos(0, 0),
        "text"
    );


    frame.add(
        new Label("Type: ${type}"),
        pos(0, 100)
    );

    frame.bind(new DataObject());

  }

  public static void main(String[] args) {
    new GuiTest().runGame();
  }

  public static class DataObject {
    private String type = "foobar";

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }
  }
}
