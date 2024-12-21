package com.berray.objects.gui;

import com.berray.event.CoreEvents;
import com.berray.math.Vec2;
import com.berray.objects.gui.layout.LayoutManager;
import com.berray.objects.guiold.PropertyResolveService;

/** Gui panel. */
public class Panel extends Container {

  /** Bound object from ourselves. */
  private Object boundObject;

  private PanelType panelType = PanelType.UNBOUND;

  public Panel(Vec2 size, LayoutManager layoutManager) {
    super(layoutManager);

    setSize(size);
    registerBoundProperty("boundObject", this::getBoundObject, this::setBoundObject);
  }

  public Object getBoundObject() {
    if (boundObject != null) {
      return boundObject;
    }
    Panel parentPanel = findParent(Panel.class);
    if (parentPanel == null) {
      return null;
    }
    return parentPanel.getBoundObject();
  }

  private void setBoundObject(Object boundObject) {
    this.boundObject = boundObject;
  }

  public void bind(Object dataObject) {
    // if the new object is exactly the same as the current...don't do anything
    if (this.boundObject == dataObject) {
      return;
    }

    // first unbind current object, if it exists
    if (this.boundObject != null) {
      trigger(CoreEvents.UNBIND, this, this.boundObject);
    }
    this.boundObject = dataObject;

    if (this.boundObject != null) {
      trigger(CoreEvents.UNBIND, this, this.boundObject);
    }
  }

  public enum PanelType {
    /** Panel is just for grouping, not for data binding. */
    UNBOUND,
    /** Bound object is set directly in the panel. */
    BOUND_OBJECT,
    /** bound object is calculated from a property in the parents panel game object. */
    BOUND_PROPERTY
  }
}
