package com.berray.event;

import java.util.List;

/**
 * Event fired when a key is pressed down (keyDown), released (keyRelease) and pressed and released (keyPress).
 *
 * @type event
 */
public class KeyEvent extends Event {
  public static final String EVENT_NAME_KEY_PRESS = "keyPress";
  public static final String EVENT_NAME_KEY_DOWN = "keyDown";
  public static final String EVENT_NAME_KEY_UP = "keyUP";

  public KeyEvent(String event, List<Object> parameters) {
    super(event, parameters);
  }

  /**
   * Returns the key code of the pressed or released key.
   */
  public int getKeyCode() {
    return (int) parameters.get(1);
  }
}
