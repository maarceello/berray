package com.berray.objects.gui;

import com.berray.objects.gui.layout.FillLayout;

public class LoopItem extends Panel {

  public LoopItem(LoopIter loopIter) {
    super(PanelType.BOUND_OBJECT);
    // loop items always have exactly one child and this child fills the whole loop item
    setLayoutManager(new FillLayout());
    set("boundObject", loopIter);
  }


}
