package com.berray.objects.gui.layout;

import com.berray.GameObject;
import com.berray.math.Rect;
import com.berray.objects.gui.Container;

import java.util.ArrayList;
import java.util.List;

public class AdjustingGridLayout implements LayoutManager {

  private int numColumns = 5;

  private int rowSpacing = 0;
  private int columnSpacing = 0;

  public void setNumColumns(int numColumns) {
    this.numColumns = numColumns;
  }

  public void setRowSpacing(int rowSpacing) {
    this.rowSpacing = rowSpacing;
  }

  public void setColumnSpacing(int columnSpacing) {
    this.columnSpacing = columnSpacing;
  }

  @Override
  public void layoutPanel(Container panel, List<GameObject> componentsToLayout, Rect destination) {
    // place game objects in rows
    List<Row> rows = new ArrayList<>();
    for (int i = 0; i < (componentsToLayout.size() + numColumns-1) / numColumns; i++) {
      rows.add(new Row(componentsToLayout.subList(i * numColumns, Math.min((i+1) * numColumns,componentsToLayout.size() ))));
    }



  }



  private static class Row {
    private final List<GameObject> gameObjects;

    public Row(List<GameObject> gameObjects) {
      this.gameObjects = gameObjects;
    }

    //public List<Integer> get
  }
}
