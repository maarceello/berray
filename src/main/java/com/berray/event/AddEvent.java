package com.berray.event;

import com.berray.GameObject;

import java.util.List;

/** Event fired when a game object is added to the scene graph. */
public class AddEvent extends Event {
  public AddEvent(List<Object> parameters) {
    super("add", parameters);
  }

  /** returns the parent to which the gameobject is added. */
  public GameObject getParent() {
    return (GameObject) parameters.get(0);
  }

  /** returns the child which was added. */
  public GameObject getChild() {
    return (GameObject) parameters.get(1);
  }

}
