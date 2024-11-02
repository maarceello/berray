package com.berray.event;

import com.berray.GameObject;

import java.util.List;

/**
 * Fired when the game object is added to the (active) scene graph. Now the 'game' is available.
 */
public class SceneGraphEvent extends Event {
  public static final String EVENT_NAME_ADDED = "sceneGraphAdded";
  public static final String EVENT_NAME_REMOVED = "sceneGraphRemoved";

  public SceneGraphEvent(String name, List<Object> parameters) {
    super(name, parameters);
  }

  public GameObject getGameObject() {
    return getParameter(1);
  }

}
