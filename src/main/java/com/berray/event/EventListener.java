package com.berray.event;

@FunctionalInterface
public interface EventListener {
  void onEvent(Event event);
}
