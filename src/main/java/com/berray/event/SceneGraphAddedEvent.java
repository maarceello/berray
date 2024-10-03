package com.berray.event;

import com.berray.GameObject;

import java.util.List;

/** Fired when the game object is added to the (active) scene graph. Now the 'game' is available. */
public class SceneGraphAddedEvent extends Event {
  public SceneGraphAddedEvent(List<Object> parameters) {
    super("sceneGraphAdded", parameters);
  }

  public GameObject getGameObject() {
    return getParameter(0);
  }

}
