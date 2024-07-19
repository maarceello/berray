package com.berray.components;

import com.berray.GameObject;
import com.berray.event.Event;

public class AreaComponent extends Component {

  public AreaComponent(String tag) {
    super("area");
  }

  @Override
  public void add(GameObject gameObject) {
    gameObject.on("hover", this::onHover);
    gameObject.on("collideUpdate", this::onCollideUpdate);

  }

  public void onHover(Event event) {

  }

  public void onCollideUpdate(Event event) {

  }
}
