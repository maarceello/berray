package com.berray.objects.gui.layout;

import com.berray.BerrayApplication;
import com.berray.math.Color;

public class AdjustingGridLayoutTest extends BerrayApplication {

  @Override
  public void initWindow() {
    width(1000);
    height(1000);
    background(Color.GRAY);
    title(getClass().getSimpleName());
  }

  @Override
  public void game() {

  }


  public static void main(String[] args) {
    new AdjustingGridLayoutTest().runGame();
  }

}