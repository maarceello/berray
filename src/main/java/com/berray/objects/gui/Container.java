package com.berray.objects.gui;

import com.berray.GameObject;
import com.berray.event.AddEvent;
import com.berray.event.CoreEvents;
import com.berray.math.Insets;
import com.berray.math.Rect;
import com.berray.math.Vec2;
import com.berray.objects.gui.laf.LookAndFeelManager;
import com.berray.objects.gui.layout.LayoutManager;
import com.berray.objects.gui.layout.NopLayoutManager;

/** Gui container which can have a border and childs. The childs position and sizes are updated by a layouter. */
public class Container extends GameObject {
  private Vec2 size;
  private LayoutManager layoutManager;
  private String border;
  protected boolean layoutDirty;
  /** Custom insets (inside the border(*/
  private Insets insets;
  /** total insets inclusive border*/
  private Insets totalInsets;
  /** Area in which components can paint and child game objects can be placed. */
  private Rect paintArea;

  private LookAndFeelManager lookAndFeelManager;

  public Container() {
    this.layoutManager = new NopLayoutManager();
    this.border = null;
    this.layoutDirty = true;
    this.insets = Insets.NONE;

    registerBoundProperty("border", this::getBorder, this::setBorder);
    registerBoundProperty("size", this::getSize, this::setSize);
    registerBoundProperty("layoutManager", this::getLayoutManager, this::setLayoutManager);
    registerPropertyGetter("render", () -> true);

    on(CoreEvents.ADD, this::processAddEvent);
  }

  public Insets getInsets() {
    return insets;
  }

  public Insets getTotalInsets() {
    return totalInsets;
  }

  public Rect getPaintArea() {
    return paintArea;
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

  protected String getBorder() {
    return border;
  }

  protected void setBorder(String border) {
    this.border = border;
    this.layoutDirty = true;
  }

  protected Vec2 getSize() {
    return size;
  }

  protected void setSize(Vec2 size) {
    this.size = size;
    layoutDirty = true;
  }

  public LookAndFeelManager getLookAndFeelManager() {
    if (lookAndFeelManager != null) {
      return lookAndFeelManager;
    }
    return game.getDefaultLookAndFeelManager();
  }

  @Override
  protected void preDrawComponents() {
    if (layoutDirty) {
      Insets borderInsets = getLookAndFeelManager().getBorderInsets(this, border);
      this.totalInsets = borderInsets.add(insets);
      paintArea = new Rect(0, 0, size.getX(), size.getY()).reduce(totalInsets);
      layoutManager.layoutPanel(this, getChildren(), paintArea);
      layoutDirty = false;
    }
    super.preDrawComponents();
  }

  @Override
  protected void postDrawComponents() {
    if (border != null) {
      LookAndFeelManager laf = getLookAndFeelManager();
      laf.drawBorder(this, border);
    }
    super.postDrawComponents();
  }
}
