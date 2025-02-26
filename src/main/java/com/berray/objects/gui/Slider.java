package com.berray.objects.gui;

import com.berray.components.CoreComponentShortcuts;
import com.berray.event.ActionEvent;
import com.berray.event.CoreEvents;
import com.berray.event.MouseEvent;
import com.berray.event.SceneGraphEvent;
import com.berray.math.MathUtil;
import com.berray.math.Vec2;
import com.berray.objects.gui.layout.NopLayoutManager;
import com.berray.objects.gui.model.SliderModel;

import java.util.ArrayList;
import java.util.List;

/** Slider gui component. */
public class Slider extends Container implements CoreComponentShortcuts {
  private SliderModel model;

  private final String actionId;

  public Slider(String actionId, Vec2 size, SliderModel model) {
    super(new NopLayoutManager());
    setSize(size);
    this.actionId = actionId;
    this.model = model;
    on(CoreEvents.SCENE_GRAPH_ADDED, this::onSceneGraphAdded);
    on(CoreEvents.MOUSE_CLICK, this::onMouseClick);
    on(CoreEvents.DRAGGING, this::onMouseDragging);

    registerBoundProperty("model", this::getModel, this::setModel);
    // alias value property from model
    registerBoundProperty("value", this::getValue, this::setValue);
  }

  public SliderModel getModel() {
    return model;
  }

  public void setModel(SliderModel model) {
    this.model = model;
  }

  private void assertModelSet() {
    if (model == null) {
      throw new IllegalStateException("slider model not set");
    }
  }

  public int getMin() {
    assertModelSet();
    Panel panel = findParent(Panel.class);
    if (panel != null) {
      return model.getMin(panel.getBoundObject());
    }
    return 0;
  }

  public int getMax() {
    assertModelSet();
    Panel panel = findParent(Panel.class);
    if (panel != null) {
      return model.getMax(panel.getBoundObject());
    }
    return 0;
  }

  public int getValue() {
    assertModelSet();
    Panel panel = findParent(Panel.class);
    if (panel != null) {
      return model.getValue(panel.getBoundObject());
    }
    return 0;
  }

  public void setValue(int value) {
    assertModelSet();
    Panel panel = findParent(Panel.class);
    if (panel != null) {
      model.setValue(panel.getBoundObject(), value);
      emitSetValueEvent(value);
    }
  }

  private void onSceneGraphAdded(SceneGraphEvent e) {
    getLookAndFeelManager().installToSlider(this);
  }

  private void onMouseDragging(MouseEvent event) {
    updateSliderPosition(event);
  }

  private void onMouseClick(MouseEvent event) {
    updateSliderPosition(event);
  }

  private void updateSliderPosition(MouseEvent event) {
    Vec2 size = getSize();
    int min = getMin();
    int max = getMax();
    Vec2 relativePos = event.getGameObjectPos();
    float percent = MathUtil.clamp(relativePos.getX() / size.getX(), 0.0f, 1.0f);
    set("value", (int) (min + (max - min) * percent));
  }


  /**
   * Fired when the button was clicked,
   * @type emit-event
   */
  private void emitSetValueEvent(int value) {
    // send action event to the next panel in the object tree
    trigger(ActionEvent.EVENT_NAME, this, actionId, value);
    Panel nextPanelInTree = findParent(Panel.class);
    if (nextPanelInTree != null) {
      nextPanelInTree.trigger(ActionEvent.EVENT_NAME, this, actionId, value);
    }
  }

  @Override
  public void addComponents(List<Object> components) {
    List<Object> allComponents = new ArrayList<>();
    List<Object> existingComponents = new ArrayList<>(this.components.values());
    existingComponents.addAll(components);

    if (!containsComponent(existingComponents, "pos") && !is("pos")) {
      allComponents.add( pos(0,0));
    }
    if (!containsComponent(existingComponents, "area") && !is("area")) {
      allComponents.add(area());
    }
    if (!containsComponent(existingComponents, "mouse") && !is("mouse")) {
      allComponents.add(mouse());
    }
    allComponents.addAll(components);
    // then add the supplied components. these may overwrite our own components.
    super.addComponents(allComponents);
  }
}
