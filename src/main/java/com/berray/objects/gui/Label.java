package com.berray.objects.gui;

import com.berray.GameObject;
import com.berray.event.*;
import com.berray.math.Color;
import com.berray.objects.guiold.PropertyResolveService;

import static com.berray.components.core.ColorComponent.color;
import static com.berray.components.core.TextComponent.text;

public class Label extends GameObject {

  /** Current text with placeholder. */
  private String label;
  /** Next panel in the scene graph. */
  private Panel panel;

  public Label(String label) {
    this.label = label;
    addComponents(
        text(label),
        color(Color.WHITE)
    );
    on(CoreEvents.UPDATE, this::processUpdate);

    on(CoreEvents.SCENE_GRAPH_ADDED, this::processScreenGraphAdded);
    on(CoreEvents.SCENE_GRAPH_REMOVED, this::processScreenGraphRemove);
    registerBoundProperty("label", this::getLabel, this::setLabel);
  }

  /**
   * Sets the labels text (which may contain placeholder).
   *
   * @type property
   */
  public void setLabel(String label) {
    this.label = label;
  }

  /**
   * Returns the labels text (which may contain placeholder).
   *
   * @type property
   */
  public String getLabel() {
    return label;
  }

  private void processScreenGraphRemove(SceneGraphEvent e) {
    this.panel = null;
  }

  private void processScreenGraphAdded(SceneGraphEvent e) {
    // find next panel which is responsible for managing the bound data objects and events
    this.panel = findParent(Panel.class);
  }

  private void processUpdate(UpdateEvent e) {
    Object parentsBoundObject = panel == null ? null : panel.getBoundObject();
    if (parentsBoundObject != null) {
      set("text", PropertyResolveService.getInstance().replaceText(label, parentsBoundObject));
    }
    else {
      set("text", label);
    }
  }

}
