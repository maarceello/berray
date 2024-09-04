package com.berray.tests.gui;

import com.berray.GameObject;
import com.berray.components.CoreComponentShortcuts;
import com.berray.event.Event;

/** Button in Gui. */
public class Button extends GameObject implements CoreComponentShortcuts {
  public String id;

  public Button(String id) {
    this.id = id;
    on("add", this::onAdd);
    addComponents(
    );
  }


  public void onAdd(Event event) {

  }


  public static Button button(String id) {
    return new Button(id);
  }
}
