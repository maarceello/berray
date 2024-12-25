package com.berray.objects.gui;

import com.berray.event.ActionEvent;
import com.berray.event.CoreEvents;
import com.berray.math.Vec2;
import com.berray.objects.gui.layout.LayoutManager;

/** Gui panel. */
public class Panel extends Container {

  /** Bound object from ourselves. */
  private Object boundObject;

  private PanelType panelType = PanelType.UNBOUND;

  protected Panel(PanelType panelType, Vec2 size, LayoutManager layoutManager) {
    super(layoutManager);

    this.panelType = panelType;
    setSize(size);
    registerBoundProperty("boundObject", this::getBoundObject, this::setBoundObject);
    on(CoreEvents.ACTION_PERFORMED, this::forwardActionPerformed);
  }

  private void forwardActionPerformed(ActionEvent actionEvent) {
    Panel parentPanel = findParent(Panel.class);
    if (parentPanel != null) {
      parentPanel.trigger(actionEvent);
    }
  }

  public PanelType getPanelType() {
    return panelType;
  }

  public Object getBoundObject() {
    if (panelType == PanelType.UNBOUND) {
      // unbound panels don't participate in data binding and therefore forward the get bound object call to the
      // next panel down the scene graph
      Panel parentPanel = findParent(Panel.class);
      return parentPanel != null ? parentPanel.getBoundObject() : null;
    }
    return boundObject;
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

  /** Creates a panel which does not contribute to data binding. */
  public static Panel panel(Vec2 size, LayoutManager layoutManager) {
    return new Panel(PanelType.UNBOUND, size, layoutManager);
  }

  /** Creates a panel in which the bound object must be set explicitly. */
  public static Panel panelWithBoundObject(Vec2 size, LayoutManager layoutManager, Object boundObject) {
    Panel panel = new Panel(PanelType.BOUND_OBJECT, size, layoutManager);
    panel.set("boundObject", boundObject);
    return panel;
  }

  /** Creates a panel in which the bound object must be set explicitly. */
  public static Panel panelWithBoundProperty(Vec2 size, LayoutManager layoutManager, String property) {
    Panel panel = new Panel(PanelType.BOUND_PROPERTY, size, layoutManager);
    panel.addComponents(
        new PropertyWatchComponent(property)
    );
    return panel;
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
