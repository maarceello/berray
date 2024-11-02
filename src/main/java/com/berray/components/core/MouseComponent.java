package com.berray.components.core;

import com.berray.GameObject;
import com.berray.event.Event;
import com.berray.event.MouseEvent;
import com.berray.math.Vec2;

import static com.berray.event.CoreEvents.*;

/**
 * Component to supply hoverEnter and hoverLeave events and drag events.
 * Note: needs "area" component so the component can check if the mouse cursor is over this game object
 */
public class MouseComponent extends Component {
  private boolean hovered = false;

  public MouseComponent() {
    super("mouse", "area");
  }

  @Override
  public void add(GameObject gameObject) {
    super.add(gameObject);
    on(SCENE_GRAPH_ADDED, this::processSceneGraphAdded);
    on(SCENE_GRAPH_REMOVED, this::processSceneGraphRemoved);
    on(HOVER_ENTER, this::processHoverEnter);
    on(HOVER_LEAVE, this::processHoverLeave);
    registerGetter("hovered", this::isHovered);
  }

  private  void processHoverEnter(MouseEvent e) {
    this.hovered = true;
  }

  private  void processHoverLeave(MouseEvent e) {
    this.hovered = false;
  }
  private void processSceneGraphAdded(Event e) {
    gameObject.getGame().getMouseManager().registerGameObject(gameObject);
  }

  private  void processSceneGraphRemoved(Event e) {
    gameObject.getGame().getMouseManager().removeGameObject(gameObject);
  }

  /**
   * Returns whether the mouse cursor is over the component (hovered).
   *
   * @type property
   */
  public boolean isHovered() {
    return hovered;
  }

  /**
   * creates a new mouse() component.
   */
  public static MouseComponent mouse() {
    return new MouseComponent();
  }
}
