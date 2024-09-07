package com.berray.objects.gui;

import com.berray.GameObject;
import com.berray.components.CoreComponentShortcuts;
import com.berray.components.core.AnchorType;
import com.berray.event.Event;
import com.berray.math.Color;
import com.berray.math.Rect;
import com.berray.math.Vec2;

import java.util.Arrays;
import java.util.List;

/**
 * Button in Gui.
 */
public class Button extends GameObject implements CoreComponentShortcuts {
  public String id;

  private boolean armed;

  private GameObject neutralChild;
  private GameObject hoverChild;
  private GameObject armedChild;
  private GameObject pressedChild;
  private GameObject text;

  public Button(String id) {
    this.id = id;
    on("add", this::onAdd);
    on("hoverEnter", this::onHoverEnter);
    on("hoverLeave", this::onHoverLeave);
    on("mousePress", this::onMousePress);
    on("mouseRelease", this::onMouseRelease);
    registerPropertyGetter("size", () -> getChildren().get(0).get("size"));
    registerPropertyGetter("render", () -> true);
  }

  private void onMouseRelease(Event event) {
    Vec2 absoluteMousePos = event.getParameter(2);
    Rect boundingBox = getBoundingBox();
    boolean stillhovered = boundingBox.contains(absoluteMousePos);
    replaceChild(0, stillhovered ? getHoverGameObject() : neutralChild);
    armed = false;
  }


  private void onMousePress(Event event) {
    replaceChild(0, getArmedGameObject());
    armed = true;
  }

  private void onHoverLeave(Event event) {
    // only process hover events when not armed
    if (!armed) {
      replaceChild(0, neutralChild);
    }
  }

  private void onHoverEnter(Event event) {
    // only process hover events when not armed
    if (!armed) {
      replaceChild(0, getHoverGameObject());
    }
  }

  private void onAdd(Event event) {
    GameObject parent = event.getParameter(0);
    // ignore add event when we're the one the child is added to
    if (parent != this) {
      addChild(neutralChild);
      text = add(
          text("foo"),
          pos(0, 0),
          anchor(AnchorType.TOP_LEFT),
          color(Color.WHITE)
      );
    }
    setTransformDirty();
  }

  @Override
  public void addComponents(List<Object> components) {
    // first add our own components
    super.addComponents(Arrays.asList(
        area(),
        mouse(),
        pos(0, 0))
    );
    // then add the supplied components. these may overwrite our own components.
    super.addComponents(components);
  }

  /**
   * Sets the game object (components) which is used when the button is neither hovered nor armed nor pressed.
   *
   * @type initialization
   */
  public Button neutral(Object... components) {
    this.neutralChild = make(components);
    return this;
  }

  /**
   * Sets the game object (components) which is used when the button is hovered
   *
   * @type initialization
   */
  public Button hover(Object... components) {
    this.hoverChild = make(components);
    return this;
  }

  /**
   * Sets the game object (components) which is used when the button is armed
   *
   * @type initialization
   */
  public Button armed(Object... components) {
    this.armedChild = make(components);
    return this;
  }

  /**
   * Sets the game object (components) which is used when the button is pressed.
   * TODO: unused at the moment.
   *
   * @type initialization
   */
  public Button pressed(Object... components) {
    this.pressedChild = make(components);
    return this;
  }

  protected GameObject getHoverGameObject() {
    return hoverChild != null ? hoverChild : neutralChild;
  }

  protected GameObject getArmedGameObject() {
    return armedChild != null ? armedChild : neutralChild;
  }

  protected GameObject getPressedGameObject() {
    return pressedChild != null ? pressedChild : neutralChild;
  }

  /**
   * creates a new button
   *
   * @param id action id to send when the button is pressed. may be <code>null</code>, then no button event is sent.
   */
  public static Button button(String id) {
    return new Button(id);
  }
}
