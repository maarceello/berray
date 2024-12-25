package com.berray.objects.gui.model;

import com.berray.objects.guiold.PropertyResolveService;

import java.util.function.Function;

public class PropertySliderModel implements SliderModel{
  private Object modelObject;
  private Function<Object, Integer> minFunction;
  private Function<Object, Integer> maxFunction;
  private String valueProperty = "value";

  public PropertySliderModel(Object modelObject) {
    this.modelObject = modelObject;
  }

  public PropertySliderModel(Object modelObject, String valueProperty) {
    this.modelObject = modelObject;
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
  public int getValue() {
    return (Integer) PropertyResolveService.getInstance().getProperty(modelObject, valueProperty);
  }

  @Override
  public void setValue(int value) {
    int min = getMin();
    int max = getMax();
    if (value > max)  {
      value = max;
    }
    if (value < min)  {
      value = min;
    }
    PropertyResolveService.getInstance().setProperty(modelObject, valueProperty, value);
  }

  @Override
  public int getMin() {
    return toInt(minFunction.apply(modelObject));
  }

  @Override
  public int getMax() {
    return toInt(maxFunction.apply(modelObject));
  }

  private int toInt(Integer integer) {
    return integer == null ? 0 : integer;
  }

  public void setModelObject(Object modelObject) {
    this.modelObject = modelObject;
  }

  public void setValueProperty(String valueProperty) {
    this.valueProperty = valueProperty;
  }
}
