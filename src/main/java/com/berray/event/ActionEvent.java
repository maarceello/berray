package com.berray.event;

import com.berray.GameObject;

import java.util.List;

/**
 * Event fired when an action is performed on a gui element. The action depends on the gui element, it could be a button
 * click or whatever.
 *
 * @type event
 */
public class ActionEvent extends Event {
  public static final String EVENT_NAME = "actionPerformed";
  public ActionEvent(List<Object> parameters) {
    super(EVENT_NAME, parameters);
  }

  /** returns the gui element on which the action was performed. */
  public GameObject getObject() {
    return getParameter(0);
  }

  /** returns the action id of the gui element. */
  public String getActionId() {
    return getParameter(1);
  }

  /** returns the new value of the gui element after the action was executed. */
  public <E> E getValue() {
    return getParameter(2);
  }
}
