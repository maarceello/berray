package com.berray.objects.gui;

import com.berray.event.EventListener;
import com.berray.event.PropertyChangeEvent;

import java.util.List;

/** Marks an object which accepts event listeners. */
public interface EventListenerCapable {
  void onPropertyChange(EventListener<? extends PropertyChangeEvent> listener);
  void onPropertyChange(EventListener<? extends PropertyChangeEvent> listener, Object owner);
  void removeListener(Object owner);
  void removeAllListeners();

  /** returns the list of properties. */
  List<String> getProperties();
  /** return the named property */
  <T> T getProperty(String name);
  /** set the named property with a new value. */
  <T> void setProperty(String name, T value);
}
