package com.berray.objects.gui.layout;

import com.berray.GameObject;
import com.berray.math.Rect;
import com.berray.math.Vec2;
import com.berray.objects.gui.Container;

import java.util.List;

/**
 * Layouts the childs in a grid specified by column width and row heights. The children are placed in the order they
 * were added to the panel in row-major order.
 * When a component is resized to the exact size of the cell, if there is a setter for the property 'size'.
 * Components without a pos property are ignored.
 * If the components need more rows than configured, the last configured row height is used for additional rows.
 */
public class GridLayout implements LayoutManager {
  private List<Integer> columns;
  private List<Integer> rows;

  private int rowSpacing = 0;
  private int columnSpacing = 0;

  public List<Integer> getColumns() {
    return columns;
  }

  public void setColumns(List<Integer> columns) {
    this.columns = columns;
  }

  public List<Integer> getRows() {
    return rows;
  }

  public void setRows(List<Integer> rows) {
    this.rows = rows;
  }

  public int getRowSpacing() {
    return rowSpacing;
  }

  public void setRowSpacing(int rowSpacing) {
    this.rowSpacing = rowSpacing;
  }

  public int getColumnSpacing() {
    return columnSpacing;
  }

  public void setColumnSpacing(int columnSpacing) {
    this.columnSpacing = columnSpacing;
  }

  @Override
  public void layoutPanel(Container panel, List<GameObject> componentsToLayout, Rect destination) {
    if (columns.isEmpty() || rows.isEmpty()) {
      throw new IllegalStateException("rows and columns must be configured. columns: "+columns+" rows: "+rows);
    }
    int row = 0;
    int column = 0;

    int x = 0;
    int y = 0;

    for (GameObject gameObject : componentsToLayout) {
      Vec2 pos = gameObject.get("pos");
      if (pos == null) {
        continue;
      }
      int cellWidth = columns.get(column);
      int cellHeight = rows.get(row);

      gameObject.set("pos", new Vec2(x, y));
      if (gameObject.isWritable("size")) {
        gameObject.set("size", new Vec2(cellWidth, cellHeight));
      }
      column++;
      x += cellWidth + columnSpacing;
      if (column >= columns.size()) {
        column = 0;
        row = (row + 1) % rows.size();
        x = 0;
        y += cellHeight + rowSpacing;
      }
    }


  }
}
