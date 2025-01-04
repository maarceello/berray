package com.berray.objects.gui;

import com.berray.GameObject;
import com.berray.event.*;
import com.berray.math.Vec2;

import java.util.List;
import java.util.function.Supplier;

import static com.berray.components.core.PosComponent2d.pos;

public class LoopPanel extends Panel {

  private Supplier<GameObject> template;

  protected LoopPanel() {
    super(PanelType.BOUND_PROPERTY);
    on(CoreEvents.PROPERTY_CHANGED, this::processPropertyChanged);
    registerProperty("template", this::getTemplate, this::setTemplate);

    on(CoreEvents.SCENE_GRAPH_ADDED, this::processSceneGraphAdded);
  }

  private void processSceneGraphAdded(SceneGraphEvent e) {
    // always refresh children when the panel is added to the scene graph
    updateChildren(get("boundObject"));
  }

  public Supplier<GameObject> getTemplate() {
    return template;
  }

  public void setTemplate(Supplier<GameObject> template) {
    this.template = template;
  }

  private void processPropertyChanged(PropertyChangeEvent e) {
    if (!e.getPropertyName().equals("boundObject")) {
      return;
    }
    updateChildren(e.getNewValue());
  }

  private void updateChildren(List<Object> objects) {
    removeAll();
    if (objects == null || template == null) {
      return;
    }

    Panel parentPanel = findParent(Panel.class);
    Object parentBoundObject = parentPanel != null ? parentPanel.getBoundObject() : null;

    for (int i = 0; i < objects.size(); i++) {
      Object item = objects.get(i);
      LoopItem itemContainer = new LoopItem(new LoopIter(parentBoundObject, i, item));
      itemContainer.add(template.get());
      add(itemContainer, pos(Vec2.origin()));
    }
  }


  /** Creates a panel in which the bound object must be set explicitly. */
  public static Panel panelLoop() {
    LoopPanel panel = new LoopPanel();
    panel.addComponents(
        new PropertyWatchComponent(null)
    );
    return panel;
  }


}
