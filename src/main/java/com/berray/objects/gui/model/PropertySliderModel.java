package com.berray.objects.gui.model;

import com.berray.objects.guiold.PropertyResolveService;

import java.util.function.Function;

public class PropertySliderModel implements SliderModel{
  private Function<Object, Integer> minFunction;
  private Function<Object, Integer> maxFunction;
  private String valueProperty;

  public PropertySliderModel(String valueProperty) {
    this.valueProperty = valueProperty;
  }

  public PropertySliderModel minFixed(int value) {
    minFunction = object -> value;
    return this;
  }

  public PropertySliderModel maxFixed(int value) {
    maxFunction = object -> value;
    return this;
  }

  public PropertySliderModel minProperty(String minProperty) {
    minFunction = object -> (Integer) PropertyResolveService.getInstance().getProperty(object, minProperty);
    return this;
  }

  public PropertySliderModel maxProperty(String maxProperty) {
    maxFunction = object -> (Integer) PropertyResolveService.getInstance().getProperty(object, maxProperty);
    return this;
  }


  @Override
  public int getValue(Object boundObject) {
    return (Integer) PropertyResolveService.getInstance().getProperty(boundObject, valueProperty);
  }

  @Override
  public void setValue(Object boundObject, int value) {
    int min = getMin(boundObject);
    int max = getMax(boundObject);
    if (value > max)  {
      value = max;
    }
    if (value < min)  {
      value = min;
    }
    PropertyResolveService.getInstance().setProperty(boundObject, valueProperty, value);
  }

  @Override
  public int getMin(Object boundObject) {
    return toInt(minFunction.apply(boundObject));
  }

  @Override
  public int getMax(Object boundObject) {
    return toInt(maxFunction.apply(boundObject));
  }

  private int toInt(Integer integer) {
    return integer == null ? 0 : integer;
  }
}
