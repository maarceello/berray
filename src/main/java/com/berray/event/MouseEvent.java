package com.berray.event;

import com.berray.math.Vec2;
import com.raylib.Raylib;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static com.raylib.Raylib.*;

/**
 * Base class for all mouse events.
 * <p>
 * Some notes for mouse event parameter.
 * <ul>
 *   <li>0. game object which sends the event</li>
 *   <li>1. window mouse position</li>
 *   <li>2. local mouse position (null for global event)</li>
 *   <li>3. parameters for specific Mouse event</li>
 * </ul>
 *
 * @type event
 */
public class MouseEvent extends Event {
  public static final String EVENT_NAME_MOUSE_CLICK = "mouseClick";
  public static final String EVENT_NAME_MOUSE_RELEASE = "mouseRelease";
  public static final String EVENT_NAME_MOUSE_PRESS = "mousePress";
  public static final String EVENT_NAME_MOUSE_MOVE = "mouseMove";
  public static final String EVENT_NAME_MOUSE_WHEEL_MOVE = "mouseWheelMove";
  public static final String EVENT_NAME_DRAG_START = "dragStart";
  public static final String EVENT_NAME_DRAGGING = "dragging";
  public static final String EVENT_NAME_DRAG_FINISH = "dragFinish";
  public static final String EVENT_NAME_HOVER = "hover";
  public static final String EVENT_NAME_HOVER_ENTER = "hoverEnter";
  public static final String EVENT_NAME_HOVER_LEAVE = "hoverLeave";

  private final Map<Button, ButtonState> buttonStateCache = new EnumMap<>(Button.class);
  /**
   * true when this event is processed an no other (game object) listener should be
   * notified of the event.
   */
  private boolean processed = false;


  public MouseEvent(String name, List<Object> parameters) {
    super(name, parameters);
  }

  public Vec2 getWindowPos() {
    return getParameter(1);
  }

  public Vec2 getGameObjectPos() {
    return getParameter(2);
  }

  public void setProcessed() {
    this.processed = true;
  }

  public boolean isProcessed() {
    return processed;
  }

  public ButtonState getButtonState(Button button) {
    // todo: move button state map to BerrayApplication#processInputs()
    ButtonState buttonState = buttonStateCache.get(button);
    if (buttonState == null) {
      boolean down = Raylib.IsMouseButtonDown(button.getRaylibId());
      if (down) {
        boolean released = Raylib.IsMouseButtonPressed(button.getRaylibId());
        buttonState = released ? ButtonState.PRESSED_AND_DOWN : ButtonState.DOWN;
      } else {
        boolean released = Raylib.IsMouseButtonReleased(button.getRaylibId());
        buttonState = released ? ButtonState.RELEASED_AND_UP : ButtonState.UP;
      }
      buttonStateCache.put(button, buttonState);
    }
    return buttonState;
  }

  public static MouseEvent createMouseEvent(String name, List<Object> parameters) {
    switch (name) {
      case EVENT_NAME_MOUSE_WHEEL_MOVE:
        return new MouseWheelEvent(name, parameters);
      default:
        return new MouseEvent(name, parameters);
    }
  }

  public enum Button {
    LEFT(MOUSE_BUTTON_LEFT),
    MIDDLE(MOUSE_BUTTON_MIDDLE),
    RIGHT(MOUSE_BUTTON_RIGHT);

    private final int raylibId;

    Button(int raylibId) {
      this.raylibId = raylibId;
    }

    int getRaylibId() {
      return raylibId;
    }
  }


  /**
   * Bits for the current button state.
   * Bit 0 indicates if the button is up (0) or down (1). Bit 1 indicates if the event happened just this frame
   * (so 1 means 'pressed' or 'released', depending on bit 0).
   */
  public enum ButtonState {
    UP(0),
    DOWN(1),
    RELEASED_AND_UP(0b11),
    PRESSED_AND_DOWN(0b10);

    private final int value;

    ButtonState(int value) {
      this.value = value;
    }

    public int getValue() {
      return value;
    }
  }
}
