package com.berray.components.core;

import com.berray.GameObject;

/**
 * Component to place the object in a specific render layer.
 */
public class LayerComponent extends Component {

  private String layer;

  public LayerComponent(String layer) {
    super("layer");
    this.layer = layer;
  }

  @Override
  public void add(GameObject gameObject) {
    registerGetter("layer", this::getLayer);
  }

  public String getLayer() {
    return layer;
  }

  public static LayerComponent layer(String layer) {
    return new LayerComponent(layer);
  }

}
