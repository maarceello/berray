package com.berray.event;

import com.berray.GameObject;

import java.util.List;

/**
 * Event fired when a game object is added to a parent. Note that this parent does not need to be part of the scene
 * graph already. .
 *
 * @type event
 */
public class AddEvent extends Event {
  public static final String EVENT_NAME = "add";
  public AddEvent(List<Object> parameters) {
    super(EVENT_NAME, parameters);
  }

  /**
   * returns the parent to which the gameobject is added.
   */
  public GameObject getParent() {
    return (GameObject) parameters.get(0);
  }

  /**
   * returns the child which was added.
   */
  public GameObject getChild() {
    return (GameObject) parameters.get(1);
  }

}
