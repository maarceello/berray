package com.berray.objects.gui.model;

import com.berray.objects.guiold.PropertyResolveService;

import java.util.Objects;

public class MultipleButtonModel implements ButtonModel {

  private String valueProperty;
  private String armedProperty;
  public void valueProperty(String valueProperty) {
    this.valueProperty = valueProperty;
  }
  public void armedProperty(String armedProperty) {
    this.armedProperty = armedProperty;
  }

  @Override
  public void setClicked(Object boundObject, Object value) {
    // if toggle, invert current click. otherwise don't persist click
    if (valueProperty != null) {
      PropertyResolveService.getInstance().setProperty(boundObject, valueProperty, value);
    }
    if (armedProperty != null) {
      PropertyResolveService.getInstance().setProperty(boundObject, armedProperty, null);
    }
  }

  @Override
  public void setArmed(Object boundObject, Object value, boolean armed) {
    if (armedProperty != null) {
      PropertyResolveService.getInstance().setProperty(boundObject, armedProperty, armed ? value : null);
    }
  }

  @Override
  public boolean getPressed(Object boundObject, Object value) {
    if (valueProperty != null) {
      Object pressedValue = PropertyResolveService.getInstance().getProperty(boundObject, valueProperty);
      return Objects.equals(pressedValue, value);
    }
    return false;
  }

  @Override
  public boolean getArmed(Object boundObject, Object value) {
    if (armedProperty != null) {
      Object armedValue = PropertyResolveService.getInstance().getProperty(boundObject, armedProperty);
      return Objects.equals(armedValue, value);
    }
    return false;
  }

  public static MultipleButtonModel radioButtonModel() {
    return new MultipleButtonModel();
  }
}
