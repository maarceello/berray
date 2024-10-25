package com.berray.event;

import com.berray.GameObject;

import java.util.List;

/** Fired when the game object is added to the (active) scene graph. Now the 'game' is available. */
public class SceneGraphAddedEvent extends Event {
  public static final String EVENT_NAME = "sceneGraphAdded";
  public SceneGraphAddedEvent(List<Object> parameters) {
    super(EVENT_NAME, parameters);
  }

  public GameObject getGameObject() {
    return getParameter(1);
  }

}
