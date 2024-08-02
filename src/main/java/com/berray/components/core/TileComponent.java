package com.berray.components.core;

public class TileComponent extends Component {
  private boolean obstacle = false;

  public TileComponent() {
    super("tile");
  }


  public TileComponent obstacle(boolean obstacle) {
    this.obstacle = obstacle;
    return this;
  }

  public static TileComponent tile() {
    return new TileComponent();
  }
}
