package com.berray.objects.gui.model;

/** Base Model interface for the slider. */
public interface SliderModel {
  int getValue();
  void setValue(int value);

  int getMin();
  int getMax();
}
