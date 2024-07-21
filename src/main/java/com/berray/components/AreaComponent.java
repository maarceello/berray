package com.berray.components;

import com.berray.GameObject;
import com.berray.event.Event;
import com.berray.math.Rect;

public class AreaComponent extends Component {

  public AreaComponent() {
    super("area");
  }

  @Override
  public void add(GameObject gameObject) {
    super.add(gameObject);
    gameObject.on("hover", this::onHover);
    gameObject.on("collideUpdate", this::onCollideUpdate);

    gameObject.registerGetter("worldArea", this::worldArea);
  }

  public void onHover(Event event) {

  }

  public void onCollideUpdate(Event event) {

  }

  public Rect worldArea () {
    // TODO: Respect fixed game objects
    // TODO: add using Polygon as area

    Rect rect = gameObject.get("localArea");
    if (rect == null) {
      return null;
    }
    return rect;
  }

  public static AreaComponent area() {
    return new AreaComponent();
  }
}
