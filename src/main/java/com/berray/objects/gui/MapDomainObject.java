package com.berray.objects.gui;

import com.berray.event.EventListener;
import com.berray.event.EventManager;
import com.berray.event.PropertyChangeEvent;

import java.util.*;

public class MapDomainObject implements EventListenerCapable {
  private Map<String, Object> properties = new HashMap<>();

  private EventManager eventManager = new EventManager();
  @Override
  public void onPropertyChange(EventListener<? extends PropertyChangeEvent> listener) {
    eventManager.addEventListener("propertyChange", listener);
  }

  @Override
  public void onPropertyChange(EventListener<? extends PropertyChangeEvent> listener, Object owner) {
    eventManager.addEventListener("propertyChange", listener, owner);
  }

  @Override
  public void removeListener(Object owner) {
    eventManager.removeListener(owner);
  }

  @Override
  public void removeAllListeners() {
    eventManager.clear();
  }

  @Override
  public List<String> getProperties() {
    return new ArrayList<>(properties.keySet());
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T getProperty(String name) {
    return (T) properties.get(name);
  }

  @Override
  public <T> void setProperty(String name, T value) {
    T old = getProperty(name);
    if (Objects.equals(old, value)) {
      return;
    }
    properties.put(name, value);
    eventManager.trigger("propertyChange", Arrays.asList(name, old, value));
  }
}
