package com.berray.objects.gui;

import com.berray.event.*;
import com.berray.math.Rect;
import com.berray.math.Vec2;
import com.berray.objects.gui.model.ButtonModel;

import java.util.ArrayList;
import java.util.List;

import static com.berray.components.core.AreaComponent.area;
import static com.berray.components.core.MouseComponent.mouse;

/** Button functionality. */
public class Button extends Container {
  private String actionId;
  private String value;
  private ButtonType buttonType;
  private ButtonModel model;

  public Button(ButtonType buttonType) {
    this.buttonType = buttonType;
    on(CoreEvents.MOUSE_PRESS, this::onMousePress);
    on(CoreEvents.SCENE_GRAPH_ADDED, this::onSceneGraphAdded);
    on(CoreEvents.MOUSE_RELEASE, this::onMouseRelease);
    registerBoundProperty("pressed", this::isPressed, this::setPressed);
    registerBoundProperty("armed", this::isArmed, this::setArmed);
    registerBoundProperty("model", this::getModel, this::setModel);
    registerBoundProperty("actionId", this::getActionId, this::setActionId);
    registerBoundProperty("value", this::getValue, this::setValue);
  }

  public Button(String actionId, Vec2 size) {
    this(ButtonType.NORMAL);
    setSize(size);
    setActionId(actionId);
  }

  private void onSceneGraphAdded(SceneGraphEvent e) {
    getLookAndFeelManager().installToButton(this);
  }


  private void onMouseRelease(MouseEvent event) {
    Panel panel = findParent(Panel.class);
    Object boundObject = panel != null ? panel.getBoundObject() : null;

    // release armed state when the mouse button is released
    set("armed", false);
    Vec2 absoluteMousePos = event.getWindowPos();
    Rect boundingBox = getBoundingBox();
    boolean stillhovered = boundingBox.contains(absoluteMousePos);
    // only accept klick when the mouse is still over the button. Discard mouse click otherwise.
    if (stillhovered) {
      model.setClicked(boundObject, value);
      emitClickEvent(model.getPressed(boundObject, value));
    }
    firePropertyChange("armed", true, model.getArmed(boundObject, value));
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
    Panel panel = findParent(Panel.class);
    Object boundObject = panel != null ? panel.getBoundObject() : null;
    model.setArmed(boundObject, value, true);
    event.setProcessed();
    firePropertyChange("armed", false, model.getArmed(boundObject, value));
  }

  @Override
  public void addComponents(List<Object> components) {
    List<Object> allComponents = new ArrayList<>();

    if (!is("area") && !containsComponent(components, "area")) {
      allComponents.add(area());
    }
    if (!is("mouse") && !containsComponent(components, "mouse")) {
      allComponents.add(mouse());
    }
    allComponents.addAll(components);
    // then add the supplied components. these may overwrite our own components.
    super.addComponents(allComponents);
  }

  public ButtonModel getModel() {
    return model;
  }

  public void setModel(ButtonModel model) {
    this.model = model;
  }

  public ButtonType getButtonType() {
    return buttonType;
  }

  public String getActionId() {
    return actionId;
  }

  public void setActionId(String actionId) {
    this.actionId = actionId;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public void setPressed(boolean pressed) {
    Panel panel = findParent(Panel.class);
    Object boundObject = panel != null ? panel.getBoundObject() : null;
    this.model.setClicked(boundObject, value);
  }

  public boolean isPressed() {
    Panel panel = findParent(Panel.class);
    Object boundObject = panel != null ? panel.getBoundObject() : null;
    return this.model.getPressed(boundObject, value);
  }

  public boolean isArmed() {
    Panel panel = findParent(Panel.class);
    Object boundObject = panel != null ? panel.getBoundObject() : null;
    return this.model.getArmed(boundObject, value);
  }

  public void setArmed(boolean armed) {
    Panel panel = findParent(Panel.class);
    Object boundObject = panel != null ? panel.getBoundObject() : null;
    this.model.setArmed(boundObject, value, armed);
  }

  public static Button button() {
    return new Button(ButtonType.NORMAL);
  }

  public static Button checkbox() {
    return new Button(ButtonType.CHECKBOX);
  }
  public static Button radioButton() {
    return new Button(ButtonType.RADIO);
  }
}
