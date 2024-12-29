package com.berray.objects.gui;

public enum PanelType {
  /**
   * Panel is just for grouping, not for data binding.
   */
  UNBOUND,
  /**
   * Bound object is set directly in the panel.
   */
  BOUND_OBJECT,
  /**
   * bound object is calculated from a property in the parents panel game object.
   */
  BOUND_PROPERTY
}
