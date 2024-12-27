package com.berray.objects.gui.model;

/** Base Model interface for the slider. */
public interface SliderModel {
  int getValue(Object boundObject);
  void setValue(Object boundObject, int value);

  int getMin(Object boundObject);
  int getMax(Object boundObject);
}
