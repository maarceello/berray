package com.berray.objects.guiold;

import com.berray.GameObject;
import com.berray.components.CoreComponentShortcuts;
import com.berray.components.addon.Slice9Component;
import com.berray.components.core.AnchorType;
import com.berray.event.AddEvent;
import com.berray.event.CoreEvents;
import com.berray.event.Event;
import com.berray.event.MouseEvent;
import com.berray.math.Color;
import com.berray.math.Rect;
import com.berray.math.Vec2;

import java.util.ArrayList;
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
  /** When true the button is always square (i.e. checkboxes) */
  private boolean square = false;

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
    on(CoreEvents.ADD, this::onAdd);
    on(CoreEvents.HOVER_ENTER, this::onHoverEnter);
    on(CoreEvents.HOVER_LEAVE, this::onHoverLeave);
    on(CoreEvents.MOUSE_PRESS, this::onMousePress);
    on(CoreEvents.MOUSE_RELEASE, this::onMouseRelease);
    registerProperty("size", this::getSize, this::setSize);
    registerPropertyGetter("render", () -> true);
    registerBoundProperty("pressed", this::getPressed, this::setPressed);
  }

  public Vec2 getSize() {
    return getChildren().get(0).get("size");
  }

  public void setSize(Vec2 size) {
    if (square) {
      float minDimension = Math.min(size.getX(), size.getY());
      size = new Vec2(minDimension, minDimension);
    }
    if (neutralChild != null) {
      neutralChild.set("size", size);
    }
    if (hoverChild != null) {
      hoverChild.set("size", size);
    }
    if (armedChild != null) {
      armedChild.set("size", size);
    }
    if (pressedChild != null) {
      pressedChild.set("size", size);
    }
  }

  private void onMouseRelease(MouseEvent event) {
    Vec2 absoluteMousePos = event.getWindowPos();
    Rect boundingBox = getBoundingBox();
    armed = false;
    boolean stillhovered = boundingBox.contains(absoluteMousePos);
    emitClickEvent(pressed);
    if (toggleButton) {
      // toggle button
      setPressed(!pressed);
      firePropertyChange("pressed", !pressed, pressed);
      if (pressed) {
        replaceChild(0, getPressedGameObject());
        return;
      }
    }

    // push button or the toggle button is released
    replaceChild(0, stillhovered && hoverChild != null ? hoverChild : neutralChild);
  }

  /**
   * Fired when the button was clicked,
   * @type emit-event
   * */
  private void emitClickEvent(boolean pressed) {
    trigger("click", this, pressed);
  }


  private void onMousePress(MouseEvent event) {
    replaceChild(0, getArmedGameObject());
    armed = true;
    event.setProcessed();
  }

  private void onHoverLeave(Event event) {
    // only process hover events when not armed
    if (!armed) {
      replaceChild(0, pressed ? getPressedGameObject() : neutralChild);
    }
  }

  private void onHoverEnter(Event event) {
    // only process hover events when not armed
    if (!armed && hoverChild != null) {
      replaceChild(0, hoverChild);
    }
  }

  private void onAdd(AddEvent event) {
    GameObject parent = event.getSource();
    // ignore add event when we're the one the child is added to
    if (parent != this) {
      addChild(neutralChild);
    }
    setTransformDirty();
  }

  @Override
  public void addComponents(List<Object> components) {
    List<Object> allComponents = new ArrayList<>();
    List<Object> existingComponents = new ArrayList<>(this.components.values());
    existingComponents.addAll(components);
    // first, add our own components
    if (!containsComponent(existingComponents, "pos")) {
      allComponents.add( pos(0,0));
    }
    if (!containsComponent(existingComponents, "area")) {
      allComponents.add(area());
    }
    if (!containsComponent(existingComponents, "mouse")) {
      allComponents.add(mouse());
    }
    allComponents.addAll(components);
    // then add the supplied components. these may overwrite our own components.
    super.addComponents(allComponents);
  }

  /** when called the button is always square. */
  public Button square() {
    this.square = true;
    return this;
  }

  /**
   * Sets the game object (components) which is used when the button is neither hovered nor armed nor pressed.
   *
   * @type initialization
   */
  public Button neutral(Object... components) {
    this.neutralChild = makeGameObject(components);
    return this;
  }

  /**
   * Sets the game object (components) which is used when the button is neither hovered nor armed nor pressed.
   *
   * @type initialization
   */
  public Button neutral(GameObject gameObject, Object... components) {
    this.neutralChild = makeGameObject(gameObject, components);
    return this;
  }

  /**
   * Sets the game object (components) which is used when the button is hovered
   *
   * @type initialization
   */
  public Button hover(Object... components) {
    this.hoverChild = makeGameObject(components);
    return this;
  }

  /**
   * Sets the game object (components) which is used when the button is hovered
   *
   * @type initialization
   */
  public Button hover(GameObject gameObject, Object... components) {
    this.hoverChild = makeGameObject(gameObject, components);
    return this;
  }

  /**
   * Sets the game object (components) which is used when the button is armed
   *
   * @type initialization
   */
  public Button armed(Object... components) {
    this.armedChild = makeGameObject(components);
    return this;
  }

  /**
   * Sets the game object (components) which is used when the button is armed
   *
   * @type initialization
   */
  public Button armed(GameObject gameObject, Object... components) {
    this.armedChild = makeGameObject(gameObject, components);
    return this;
  }

  /**
   * Sets the game object (components) which is used when the button is pressed.
   *
   * @type initialization
   */
  public Button pressed(Object... components) {
    this.pressedChild = makeGameObject(components);
    return this;
  }

  /**
   * Sets the game object (components) which is used when the button is pressed.
   *
   * @type initialization
   */
  public Button pressed(GameObject gameObject, Object... components) {
    this.pressedChild = makeGameObject(gameObject, components);
    return this;
  }

  /**
   * Adds .
   * TODO: unused at the moment.
   *
   * @type initialization
   */
  public Button slice9(String assetName, String text, Vec2 size, int slice9ComponentSize, Color neutralColor, Color hoverColor) {
    this.neutralChild = makeSlice9Component(assetName, size, slice9ComponentSize, Vec2.origin(), text, neutralColor);
    this.hoverChild = makeSlice9Component(assetName, size, slice9ComponentSize, Vec2.origin(), text, hoverColor);
    this.armedChild = makeSlice9Component(assetName, size, slice9ComponentSize, new Vec2(5, 5), text, hoverColor);
    this.pressedChild = makeSlice9Component(assetName, size, slice9ComponentSize, new Vec2(2, 2), text, neutralColor);
    return this;
  }

  private GameObject makeSlice9Component(String assetName, Vec2 size, int slice9ComponentSize, Vec2 pos, String text, Color color) {
    GameObject button = makeGameObject(
        Slice9Component.slice9(assetName, size, slice9ComponentSize),
        area(),
        pos(pos),
        anchor(AnchorType.TOP_LEFT),
        color(color)
    );

    button.add(
        text(text),
        pos(100, 25),
        anchor(AnchorType.CENTER)
    );

    return button;
  }

  protected GameObject getArmedGameObject() {
    return armedChild != null ? armedChild : neutralChild;
  }

  protected GameObject getPressedGameObject() {
    return pressedChild != null ? pressedChild : neutralChild;
  }

  public boolean getPressed() {
    return pressed;
  }

  public void setPressed(boolean pressed) {
    this.pressed = pressed;
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
