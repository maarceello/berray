package com.berray.objects.gui;

import com.berray.event.CoreEvents;
import com.berray.math.Vec2;
import com.berray.objects.gui.layout.LayoutManager;
import com.berray.objects.guiold.PropertyResolveService;

/** Gui panel. */
public class Panel extends Container {

  /** Bound object from ourselves. */
  private Object boundObject;
  /** property which should be used from the parent bound object. */
  private String parentBoundObjectProperty;

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
    Object parentBoundObject = parentPanel.getBoundObject();
    if (parentBoundObject == null) {
      return null;
    }
    if (parentBoundObjectProperty == null) {
      return parentBoundObject;
    }
    return PropertyResolveService.getProperty(parentBoundObject, parentBoundObjectProperty);
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

  public void unbind() {
    bind(null);
  }
}
