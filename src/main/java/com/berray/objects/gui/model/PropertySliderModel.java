package com.berray.objects.gui.model;

import com.berray.objects.guiold.PropertyResolveService;

public class PropertySliderModel implements SliderModel{
  private Object modelObject;
  private String minProperty = "min";
  private String maxProperty = "max";
  private String valueProperty = "value";

  public PropertySliderModel(Object modelObject) {
    this.modelObject = modelObject;
  }

  public PropertySliderModel(Object modelObject, String minProperty, String maxProperty, String valueProperty) {
    this.modelObject = modelObject;
    this.minProperty = minProperty;
    this.maxProperty = maxProperty;
    this.valueProperty = valueProperty;
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
    return (Integer) PropertyResolveService.getInstance().getProperty(modelObject, minProperty);
  }

  @Override
  public int getMax() {
    Integer max = (Integer)  PropertyResolveService.getInstance().getProperty(modelObject, maxProperty);
    return max == null ? 0 : max;
  }

  public void setModelObject(Object modelObject) {
    this.modelObject = modelObject;
  }

  public void setMinProperty(String minProperty) {
    this.minProperty = minProperty;
  }

  public void setMaxProperty(String maxProperty) {
    this.maxProperty = maxProperty;
  }

  public void setValueProperty(String valueProperty) {
    this.valueProperty = valueProperty;
  }
}
