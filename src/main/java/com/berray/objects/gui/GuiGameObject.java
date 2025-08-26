package com.berray.objects.gui;

import com.berray.GameObject;
import com.berray.math.Insets;
import com.berray.math.Rect;
import com.berray.math.Vec2;
import com.berray.objects.gui.laf.LookAndFeelManager;

import java.util.Objects;

/**
 * A game object which can be used in the gui.
 * <p>
 * Gui game object have some additional properties:
 * <ul>
 *   <li>min size - minimum size the the component needs to be. Each coordinate may be 0 to indicate there is
 *   no minimum size or -1 to indicate that the minimum size cannot be determined at the moment.</li>
 * </ul>
 */
public class GuiGameObject extends GameObject {
  private LookAndFeelManager lookAndFeelManager;
  /**
   * name of the border for the gui object.
   */
  private String border;
  /**
   * Custom insets (inside the border)
   */
  private Insets insets;
  /**
   * total insets inclusive border
   */
  private Insets totalInsets;
  /**
   * Area in which components can paint and child game objects can be placed.
   */
  private Rect paintArea;

  /**
   * Default gui objects don't have any minimum size.
   */
  private Vec2 minSize = new Vec2(-1, -1);
  private Vec2 size;

  public GuiGameObject() {
    registerBoundProperty("size", this::getSize, this::setSize);
    registerPropertyGetter("minSize", this::getMinSize);

    this.border = null;
    this.insets = Insets.NONE;
  }

  protected Vec2 getSize() {
    return size;
  }

  protected Vec2 getMinSize() {
    return minSize;
  }


  protected void setSize(Vec2 size) {
    this.size = size;
  }

  public LookAndFeelManager getLookAndFeelManager() {
    if (lookAndFeelManager != null) {
      return lookAndFeelManager;
    }
    return game.getDefaultLookAndFeelManager();
  }


  public Insets getInsets() {
    return insets;
  }

  public void setInsets(Insets insets) {
    Objects.requireNonNull(insets, "insets may not be null");
    this.insets = insets;
    this.totalInsets = null;
    this.paintArea = null;
  }

  protected String getBorder() {
    return border;
  }

  protected void setBorder(String border) {
    this.border = border;
    this.totalInsets = null;
    this.paintArea = null;
  }

  public Insets getTotalInsets() {
    calculateInsets();
    return totalInsets;
  }

  public Rect getPaintArea() {
    calculateInsets();
    return paintArea;
  }

  private void calculateInsets() {
    if (totalInsets == null) {
      Insets borderInsets = getLookAndFeelManager().getBorderInsets(this, border);
      this.totalInsets = borderInsets.add(insets);
      paintArea = new Rect(0, 0, size.getX(), size.getY()).reduce(totalInsets);
    }
  }

  @Override
  protected void postDrawComponents() {
    getLookAndFeelManager().drawBorder(this, getBorder());
    super.postDrawComponents();
  }

}
