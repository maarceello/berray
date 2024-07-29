package com.berray.components.core;

import com.berray.GameObject;

/**
 * # AnchorComponent
 *
 * {@link AnchorComponent#anchor(AnchorType)} provides an anchor point for rendering shapes.
 *
 * # Properties
 *
 * - anchor (read only) - returns the current anchor point for this game object
 *
 * # Events
 *
 * none
 */
public class AnchorComponent extends Component {
  private AnchorType anchorType;

  public AnchorComponent(AnchorType anchorType) {
    super("anchor");
    this.anchorType = anchorType;
  }

  public AnchorType getAnchor() {
    return anchorType;
  }

  public void setAnchor(AnchorType anchorType) {
    this.anchorType = anchorType;
  }

  @Override
  public void add(GameObject gameObject) {
    gameObject.registerMethod("anchor", this::getAnchor, this::setAnchor);
  }

  public static AnchorComponent anchor(AnchorType anchorType) {
    return new AnchorComponent(anchorType);
  }
}