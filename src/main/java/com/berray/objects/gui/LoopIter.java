package com.berray.objects.gui;

public class LoopIter {
  private final int index;
  private final Object item;
  private final Object parent;

  public LoopIter(Object parent, int index, Object item) {
    this.parent = parent;
    this.index = index;
    this.item = item;
  }

  public Object getParent() {
    return parent;
  }

  public int getIndex() {
    return index;
  }

  public Object getItem() {
    return item;
  }
}
