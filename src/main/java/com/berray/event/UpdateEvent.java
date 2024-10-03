package com.berray.event;

import com.berray.GameObject;

import java.util.List;

/** Event fired each frame to update the scene graph. */
public class UpdateEvent extends Event {
  public UpdateEvent(List<Object> parameters) {
    super("update", parameters);
  }

  public GameObject getGameObject() {
    return (GameObject) parameters.get(0);
  }

}
