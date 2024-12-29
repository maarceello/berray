package com.berray.objects.gui.model;

/**
 * Model for all kinds of buttons.
 */
public interface ButtonModel {

  /**
   * The button was clicked. Depending on the type of the button it remains pressed or releases itself.
   */
  void setClicked(Object boundObject, Object id);

  /**
   * Sets whether the mouse is down on the button but not yet released (aka 'armed'). When the button is released,
   * the button is @link {@link #setClicked(Object, Object)}.
   */
  void setArmed(Object boundObject, Object id, boolean pressed);

  /**
   * Returns whether the button is armed, but not yet pressed.
   *
   * @return
   */
  boolean getArmed(Object boundObject, Object id);
  /**
   * Returns whether the button is in the pressed state or not. This may be because the button is armed or because it
   * is pressed and should remain pressed.
   */
  boolean getPressed(Object boundObject, Object id);

}
