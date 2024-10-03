package com.berray.event;

import java.util.List;

/** Event fired when a key is pressed down (keyDown), releases (keyRelease) and pressed and released (keyPress). */
public class KeyEvent extends Event {
  public KeyEvent(String event, List<Object> parameters) {
    super(event, parameters);
  }

  public int getKeyCode() {
    return (int) parameters.get(0);
  }
}
