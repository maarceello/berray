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
  /**
   * Id to send to Gui Framework when the button is pressed.
   */
  private final String id;
  /**
   * when true the button is a toggle button (with 2 states), when false the button is a
   * push button.
   */
  private final boolean toggleButton;

  /**
   * armed state is when the mouse is down on the button, but not yet released.
   */
  private boolean armed = false;
  /**
   * used for toggle button: the button is pressed.
   */
  private boolean pressed = false;

  private GameObject neutralChild;
  private GameObject hoverChild;
  private GameObject armedChild;
  private GameObject pressedChild;
  private GameObject text;

  public Button(String id, boolean toggleButton) {
    this.id = id;
    this.toggleButton = toggleButton;
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
    armed = false;
    boolean stillhovered = boundingBox.contains(absoluteMousePos);
    if (toggleButton) {
      // toggle button
      pressed = !pressed;
      if (pressed) {
        replaceChild(0, getPressedGameObject());
        return;
      }
    }
    // push button or the toggle button is released
    replaceChild(0, stillhovered ? getHoverGameObject() : neutralChild);
  }


  private void onMousePress(Event event) {
    replaceChild(0, getArmedGameObject());
    armed = true;
  }

  private void onHoverLeave(Event event) {
    // only process hover events when not armed
    if (!armed) {
      replaceChild(0, pressed ? getPressedGameObject() : neutralChild);
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
   * Sets the game object (components) which is used when the button is neither hovered nor armed nor pressed.
   *
   * @type initialization
   */
  public Button neutral(GameObject gameObject, Object... components) {
    this.neutralChild = make(gameObject, components);
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
   * Sets the game object (components) which is used when the button is hovered
   *
   * @type initialization
   */
  public Button hover(GameObject gameObject, Object... components) {
    this.hoverChild = make(gameObject, components);
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
   * Sets the game object (components) which is used when the button is armed
   *
   * @type initialization
   */
  public Button armed(GameObject gameObject, Object... components) {
    this.armedChild = make(gameObject, components);
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

  /**
   * Sets the game object (components) which is used when the button is pressed.
   * TODO: unused at the moment.
   *
   * @type initialization
   */
  public Button pressed(GameObject gameObject, Object... components) {
    this.pressedChild = make(gameObject, components);
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
   * Creates a new push button. The button can be pressed and is released when the mouse button is released.
   * Note that the "pressed" component is not used in this button type.
   *
   * @param id action id to send when the button is pressed. may be <code>null</code>, then no button event is sent.
   */
  public static Button pushButton(String id) {
    return new Button(id, false);
  }

  /**
   * Creates a new toggle button. The button can be pressed and is on the "pressed" state. Pressing the button
   * again moves the button in the neutral state again.
   *
   * @param id action id to send when the button is pressed. may be <code>null</code>, then no button event is sent.
   */
  public static Button toggleButton(String id) {
    return new Button(id, true);
  }

  /**
   * Creates a new button.
   *
   * @param id action id to send when the button is pressed. may be <code>null</code>, then no button event is sent.
   */
  public static Button button(String id, boolean toggleButton) {
    return new Button(id, toggleButton);
  }
}
