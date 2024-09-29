package com.berray.objects.gui.panel;

import com.berray.GameObject;
import com.berray.components.core.AnchorType;
import com.berray.math.Color;

import java.util.function.Function;

public class CellDefinition {
  /**
   * Function to create the game object for this cell.
   */
  private Function<PanelBuilder, GameObject> cellBuilder;
  /**
   * number of cells this game object should span.
   */
  private int columnSpan = 1;
  private AnchorType anchorType;
  private Color backgroundColor;

  public CellDefinition(Function<PanelBuilder, GameObject> cellBuilder, int columnSpan, AnchorType anchorType, Color backgroundColor) {
    this.cellBuilder = cellBuilder;
    this.columnSpan = columnSpan;
    this.anchorType = anchorType;
    this.backgroundColor = backgroundColor;
  }

  public Function<PanelBuilder, GameObject> getCellBuilder() {
    return cellBuilder;
  }

  public int getColumnSpan() {
    return columnSpan;
  }

  public Color getBackgroundColor() {
    return backgroundColor;
  }

  public AnchorType getAnchorType() {
    return anchorType;
  }
}
