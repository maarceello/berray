package com.berray;

/** Order in which to draw the children of a game object. */
public enum DrawOrder {
  /**
   * Draw nodes and their silblings and then draw the children.
   */
  BREATH_FIRST,
  /**
   * draw node and its children and then the silblings.
   */
  DEPTH_FIRST;
}
