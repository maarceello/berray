package com.berray.objects.gui;

import com.berray.event.AddEvent;
import com.berray.event.CoreEvents;
import com.berray.math.Vec2;
import com.berray.objects.gui.layout.LayoutManager;
import com.berray.objects.gui.layout.NopLayoutManager;

/** Gui container which can have a border and childs. The childs position and sizes are updated by a layouter. */
public class Container extends GuiGameObject {
  private LayoutManager layoutManager;
  protected boolean layoutDirty;


  public Container() {
    this.layoutManager = new NopLayoutManager();
    this.layoutDirty = true;

    registerBoundProperty("border", this::getBorder, this::setBorder);
    registerBoundProperty("layoutManager", this::getLayoutManager, this::setLayoutManager);
    registerPropertyGetter("render", () -> true);

    on(CoreEvents.ADD, this::processAddEvent);
    onPropertyChange("size", (event) -> layoutDirty = true );
  }


  private void processAddEvent(AddEvent event) {
    this.layoutDirty = true;
  }

  public LayoutManager getLayoutManager() {
    return layoutManager;
  }

  protected void setLayoutManager(LayoutManager layoutManager) {
    this.layoutManager = layoutManager;
    this.layoutDirty = true;
  }

  @Override
  protected void setBorder(String border) {
    super.setBorder(border);
    layoutDirty = true;
  }

  @Override
  protected void preDrawComponents() {
    Vec2 size = getSize();
    if (size == null) {
      throw new IllegalStateException("cannot layout container '"+this.getClass().getSimpleName()+"' with tags "+getTags()+": size is null");
    }
    if (layoutDirty) {
      System.out.println("layout "+getClass().getSimpleName()+" with tags "+getTags());
      layoutManager.layoutPanel(this, getChildren(), getPaintArea());
      layoutDirty = false;
    }
    getLookAndFeelManager().clearBackground(this);
    super.preDrawComponents();
  }
}
