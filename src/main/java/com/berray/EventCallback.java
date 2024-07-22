package com.berray;

@FunctionalInterface
public interface EventCallback {
  void on(GameObject gameObject);
}
