package com.berray.components.core;

import java.util.List;

/** Action base class. */
public class Action {
  private final List<Object> params;

  public Action(List<Object> params) {
    this.params = params;
  }

  @SuppressWarnings("unchecked")
  protected <E> E getParameter(int index) {
    if (params.size() <= index) {
      return null;
    }
    return (E) params.get(index);
  }
}
