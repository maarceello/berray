package com.berray.components.core;

import com.berray.GameObject;

/**
 * {@link AnchorComponent#anchor(AnchorType)} provides an anchor point for rendering shapes.
 */
public class AnchorComponent extends Component {
  private AnchorType anchorType;

  public AnchorComponent(AnchorType anchorType) {
    super("anchor");
    this.anchorType = anchorType;
  }

  @Override
  public void add(GameObject gameObject) {
    registerBoundProperty("anchor", this::getAnchor, this::setAnchor);
  }

  /**
   * Returns the current anchor type.
   *
   * @type property
   */
  public AnchorType getAnchor() {
    return anchorType;
  }

  /**
   * Sets the current anchor type.
   *
   * @type property
   */
  public void setAnchor(AnchorType anchorType) {
    this.anchorType = anchorType;
  }

  /**
   * Creates a new anchor component.
   *
   * @param anchorType
   * @return
   * @type creator
   */
  public static AnchorComponent anchor(AnchorType anchorType) {
    return new AnchorComponent(anchorType);
  }
}
