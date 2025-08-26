package com.berray.objects.gui.layout;

import com.berray.BerrayApplication;
import com.berray.assets.CoreAssetShortcuts;
import com.berray.components.CoreComponentShortcuts;
import com.berray.math.Color;
import com.berray.math.Vec2;
import com.berray.objects.gui.Frame;
import com.berray.objects.gui.GuiPanelReader;

public class RowLayoutApplication extends BerrayApplication implements CoreComponentShortcuts, CoreAssetShortcuts {

  @Override
  public void initWindow() {
    width(1000);
    height(1000);
    background(Color.GRAY);
    title(getClass().getSimpleName());
  }

  @Override
  public void game() {

    loadSprite("berry", "resources/berry.png");


    GuiPanelReader reader = new GuiPanelReader();
    Frame gui = reader.read(getClass().getResourceAsStream("/row-layout-test.yaml"));
    gui.set("pos", new Vec2(100,100));
    add(gui);



  }

  public static void main(String[] args) {
    new RowLayoutApplication().runGame();
  }


}