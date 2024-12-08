package com.berray.objects.gui;

import com.berray.GameObject;
import com.berray.event.*;
import com.berray.math.Color;
import com.berray.objects.guiold.PropertyResolveService;

import static com.berray.components.core.ColorComponent.color;
import static com.berray.components.core.TextComponent.text;

public class Label extends GameObject {

  /** Current text with placeholder. */
  private final String text;
  /** Data object which is currently bound. */
  private Object boundObject;
  /** Next panel in the scene graph. */
  private Panel panel;

  public Label(String text) {
    this.text = text;
    addComponents(
        text(text),
        color(Color.WHITE)
    );
    on(CoreEvents.UPDATE, this::processUpdate);

    on(CoreEvents.SCENE_GRAPH_ADDED, this::processScreenGraphAdded);
    on(CoreEvents.SCENE_GRAPH_REMOVED, this::processScreenGraphRemove);
  }

  private void processScreenGraphRemove(SceneGraphEvent e) {
    // when we're removed from the scene graph:
    // - remove listener from panel
    // - delete references to panel and data object
    if (this.panel != null) {
      panel.removeListener(this);
    }
    this.panel = null;
    this.boundObject = null;
  }

  private void processScreenGraphAdded(SceneGraphEvent e) {
    // find next panel which is responsible for managing the bound data objects and events
    this.panel = findParent(Panel.class);
    // do we have a panel? add listeners and keep a reference to the currently bound data object.
    if (panel != null) {
      panel.on(CoreEvents.BIND, this::processBind, this);
      panel.on(CoreEvents.UNBIND, this::processUnbind, this);
      this.boundObject = panel.get("boundObject");
    }
  }

  private void processUpdate(UpdateEvent e) {
    Panel panel = findParent(Panel.class);
    Object foo = panel == null ? null : panel.getBoundObject();
    if (boundObject != null) {
      set("text", PropertyResolveService.replaceText(text, boundObject));
    }
    else {
      set("text", text);
    }
  }

  private void processUnbind(BindEvent e) {
    this.boundObject = null;
  }

  private void processBind(BindEvent e) {
    this.boundObject = e.getBindTarget();
  }
}
