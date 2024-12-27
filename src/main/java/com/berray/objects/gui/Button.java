package com.berray.objects.gui;

import com.berray.event.*;
import com.berray.math.Rect;
import com.berray.math.Vec2;
import com.berray.objects.gui.layout.NopLayoutManager;

import java.util.ArrayList;
import java.util.List;

import static com.berray.components.core.AreaComponent.area;
import static com.berray.components.core.MouseComponent.mouse;

/** Button functionality. */
public class Button extends Container {
  private String actionId;
  private boolean armed;
  private boolean pressed;
  private boolean toggleButton;

  public Button(String actionId, Vec2 size) {
    super(new NopLayoutManager());
    setSize(size);
    this.actionId = actionId;
    on(CoreEvents.MOUSE_PRESS, this::onMousePress);
    on(CoreEvents.SCENE_GRAPH_ADDED, this::onSceneGraphAdded);
    on(CoreEvents.MOUSE_RELEASE, this::onMouseRelease);
    registerBoundProperty("pressed", this::isPressed, this::setPressed);
    registerBoundProperty("armed", this::isArmed, this::setArmed);
  }

  private void onSceneGraphAdded(SceneGraphEvent e) {
    getLookAndFeelManager().installToButton(this);
  }


  private void onMouseRelease(MouseEvent event) {
    armed = false;
    Vec2 absoluteMousePos = event.getWindowPos();
    Rect boundingBox = getBoundingBox();
    boolean stillhovered = boundingBox.contains(absoluteMousePos);
    // only accept klick when the mouse is still over the button. Discard mouse click otherwise.
    if (stillhovered) {
      emitClickEvent(!pressed);
      if (toggleButton) {
        // toggle button
        setPressed(!pressed);
        firePropertyChange("pressed", !pressed, pressed);
      }
    }
    firePropertyChange("armed", !armed, armed);
  }

  /**
   * Fired when the button was clicked,
   * @type emit-event
   */
  private void emitClickEvent(boolean pressed) {
    trigger("click", this, pressed);
    // send action event to our own listeners
    trigger(ActionEvent.EVENT_NAME, this, actionId, pressed);
    // send action event to the next panel in the object tree
    Panel nextPanelInTree = findParent(Panel.class);
    if (nextPanelInTree != null) {
      nextPanelInTree.trigger(ActionEvent.EVENT_NAME, this, actionId, pressed);
    }
  }


  private void onMousePress(MouseEvent event) {
    armed = true;
    event.setProcessed();
    firePropertyChange("armed", !armed, armed);
  }

  @Override
  public void addComponents(List<Object> components) {
    List<Object> allComponents = new ArrayList<>();
    List<Object> existingComponents = new ArrayList<>(this.components.values());
    existingComponents.addAll(components);
    // first, add our own components
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

  public void setPressed(boolean pressed) {
    this.pressed = pressed;
  }

  public boolean isPressed() {
    return pressed;
  }

  public boolean isArmed() {
    return armed;
  }

  public void setArmed(boolean armed) {
    this.armed = armed;
  }

  @Override
  protected void preDrawComponents() {
    super.preDrawComponents();
    getLookAndFeelManager().clearBackground(this);
  }
}
