package com.berray.objects.gui.model;

import com.berray.objects.guiold.PropertyResolveService;

/** Button model for a single button. The button may persist its click state (click button, checkbox) or not (click button). */
public class SingleButtonModel implements ButtonModel {
  private final boolean toggle;
  private boolean armed;
  private boolean pressed;

  private String valueProperty;

  public SingleButtonModel(boolean toggle) {
    this.toggle = toggle;
  }

  public void valueProperty(String valueProperty) {
    this.valueProperty = valueProperty;
  }

  @Override
  public void setClicked(Object boundObject, Object id) {
    // if toggle, invert current click. otherwise don't persist click
    this.pressed = toggle ? !pressed : false ;
    this.armed = false;
    if (valueProperty != null) {
      PropertyResolveService.getInstance().setProperty(boundObject, valueProperty, pressed);
    }
  }

  @Override
  public void setArmed(Object boundObject, Object id, boolean armed) {
    this.armed = armed;
  }

  @Override
  public boolean getPressed(Object boundObject, Object id) {
    boolean localPressed = pressed;
    // if we should use a bound property for 'pressed' state: use the bound property instead our local property.
    if (valueProperty != null) {
      Boolean property = (Boolean) PropertyResolveService.getInstance().getProperty(boundObject, valueProperty);
      localPressed = property == Boolean.TRUE;
    }

    return armed || localPressed;
  }

  @Override
  public boolean getArmed(Object boundObject, Object id) {
    return armed;
  }

  public static ButtonModel clickButtonModel() {
    return new SingleButtonModel(false);
  }

  public static ButtonModel toggleButtonModel() {
    return new SingleButtonModel(true);
  }
  public static SingleButtonModel checkboxButtonModel() {
    return new SingleButtonModel(true);
  }
}
