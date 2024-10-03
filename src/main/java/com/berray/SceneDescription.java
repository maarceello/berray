package com.berray;

import com.berray.GameObject;
import com.berray.event.EventListener;

public interface SceneDescription {
  GameObject add(Object... components);

  <E extends GameObject> E add(E gameObject, Object... components);


  void on(String event, EventListener listener);

  <E> E getParameter(int paramNr);

  void onKeyPress(EventListener eventListener);
}
