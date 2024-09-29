package com.berray.objects.gui.panel;

import com.berray.GameObject;
import com.berray.components.core.AnchorComponent;
import com.berray.components.core.AnchorType;
import com.berray.math.Color;
import com.berray.math.Vec2;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static com.berray.components.core.AnchorComponent.anchor;
import static com.berray.components.core.ColorComponent.color;
import static com.berray.components.core.PosComponent2d.pos;
import static com.berray.components.core.RectComponent.rect;

public class RowBuilder {
  private final List<CellDefinition> cellDefinitions = new ArrayList<>();
  private final float height;
  private int columnSpan = 1;
  private AnchorType align = null;
  private Color backgroundColor = null;

  public RowBuilder(float height) {
    this.height = height;
  }

  public RowBuilder colSpan(int numberOfColumns) {
    this.columnSpan = numberOfColumns;
    return this;
  }

  public RowBuilder align(AnchorType align) {
    this.align = align;
    return this;
  }

  public RowBuilder background(Color color) {
    this.backgroundColor = color;
    return this;
  }


  private RowBuilder addCell(Function<PanelBuilder, GameObject> builder) {
    cellDefinitions.add(new CellDefinition(builder, columnSpan, align, backgroundColor));
    // reset columnspan, alignment and background color
    columnSpan = 1;
    align = null;
    backgroundColor = null;
    return this;
  }


  /**
   * add label with placeholder.
   */
  public RowBuilder label(String text) {
    return addCell(DefaultBuilders.labelBuilder(text, null));

  }

  /**
   * add label with placeholder.
   */
  public RowBuilder label(String text, Color foregroundColor) {
    return addCell(DefaultBuilders.labelBuilder(text, foregroundColor));
  }

  /**
   * add slider
   */
  public RowBuilder slider(String property, float min, float max) {
    return addCell(DefaultBuilders.sliderBuilder(property, min, max));
  }

  public RowBuilder skip() {
    return addCell(((panelBuilder) -> null));
  }

  public List<CellDefinition> getCellDefinitions() {
    return cellDefinitions;
  }

  public float getHeight() {
    return height;
  }

  public GameObject buildGameObject(PanelBuilder panelBuilder) {
    GameObject row = GameObject.makeGameObject(
        pos(0, 0),
        AnchorComponent.anchor(AnchorType.TOP_LEFT)
    );

    // count number of cells in this row
    int usedCells = cellDefinitions.stream().mapToInt(CellDefinition::getColumnSpan).sum();
    if (usedCells > panelBuilder.columnWidths.size()) {
      throw new IllegalStateException("too many cells for row: there are " + panelBuilder.columnWidths.size() + " columns, but " + cellDefinitions.size() + " cells should be occupied.");
    }

    int columnPos = 0;
    int column = 0;
    for (CellDefinition cellDefinition : cellDefinitions) {
      float cellWidth = (float) panelBuilder.columnWidths.subList(column, column + cellDefinition.getColumnSpan())
          .stream().mapToDouble(i -> i).sum();
      GameObject gameObject = cellDefinition.getCellBuilder().apply(panelBuilder);
      if (gameObject != null) {
        AnchorType cellAnchorType = cellDefinition.getAnchorType();
        if (cellAnchorType == null) {
          cellAnchorType = AnchorType.TOP_LEFT;
        }

        float anchorX = columnPos + cellWidth / 2.0f + cellAnchorType.getX() * cellWidth / 2.0f;

        if (gameObject.is("pos")) {
          gameObject.set("pos", new Vec2(anchorX, 0));
        } else {
          gameObject.addComponents(pos(new Vec2(anchorX, 0)));
        }

        if (gameObject.is("anchor")) {
          gameObject.set("anchor", cellAnchorType);
        } else {
          gameObject.addComponents(AnchorComponent.anchor(cellAnchorType));
        }

        if (gameObject.canWrite("size")) {
          gameObject.set("size", new Vec2(cellWidth, 20));
        }

        if (cellDefinition.getBackgroundColor() != null) {
          GameObject frame = GameObject.makeGameObject(
              pos(new Vec2(columnPos, 0)),
              rect(cellWidth, 20).fill(true),
              color(cellDefinition.getBackgroundColor()),
              anchor(AnchorType.TOP_LEFT)
          );
          frame.addChild(gameObject);
          gameObject = frame;
        }

        row.addChild(gameObject);
      }

      column += cellDefinition.getColumnSpan();
      columnPos += cellWidth;
    }

    return row;
  }

  public static RowBuilder makeRow(float height) {
    return new RowBuilder(height);
  }
}
