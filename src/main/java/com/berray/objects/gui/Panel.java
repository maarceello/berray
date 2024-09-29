package com.berray.objects.gui;

import com.berray.GameObject;
import com.berray.components.core.AnchorComponent;
import com.berray.components.core.AnchorType;
import com.berray.components.core.ColorComponent;
import com.berray.math.Color;
import com.berray.math.Vec2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static com.berray.components.core.AnchorComponent.anchor;
import static com.berray.components.core.ColorComponent.color;
import static com.berray.components.core.PosComponent2d.pos;
import static com.berray.components.core.RectComponent.rect;

/**
 * A gui panel which organizes its
 */
public class Panel extends GameObject {
  /**
   * GameObject which is placed on empty spots.
   */
  private static final GameObject EMPTY = new GameObject();

  public float[] columnWidths;
  public float[] rowHeights;

  public Panel(float[] columnWidths) {
  }


  public static class PanelBuilder implements GameObjectBuilder {
    public List<Float> columnWidths = new ArrayList<>();
    public List<Float> rowHeights = new ArrayList<>();
    private List<RowBuilder> rows = new ArrayList<>();
    private EventListenerCapable dataObject;

    /**
     * Size of the frame in pixels.
     */
    private float frameSize = 0;
    /**
     * Color of the frame.
     */
    private Color frameColor;

    private Color backgroundColor;
    private Color foregroundColor;

    public PanelBuilder(Float... columnWidths) {
      this.columnWidths = Arrays.asList(columnWidths);
    }

    public PanelBuilder bind(EventListenerCapable dataObject) {
      this.dataObject = dataObject;
      return this;
    }

    public PanelBuilder color(Color backgroundColor, Color foregroundColor) {
      this.backgroundColor = backgroundColor;
      this.foregroundColor = foregroundColor;
      return this;
    }

    public PanelBuilder frame(float size, Color frameColor) {
      this.frameSize = size;
      this.frameColor = frameColor;
      return this;
    }

    public PanelBuilder row(float height, RowBuilder builder) {
      if (builder.cellDefinitions.size() > columnWidths.size()) {
        throw new IllegalStateException("too many game objects for row: there are " + columnWidths.size() + " columns, but " + builder.cellDefinitions.size() + " gui elements should be placed.");
      }
      rowHeights.add(height);
      rows.add(builder);
      return this;
    }

    public EventListenerCapable getDataObject() {
      return dataObject;
    }

    public Color getForegroundColor() {
      return foregroundColor;
    }

    public Color getBackgroundColor() {
      return backgroundColor;
    }

    @Override
    public GameObject buildGameObject() {
      int rowPos = 0;
      GameObject panel = makeGameObject(
          pos(0, 0),
          anchor(AnchorType.TOP_LEFT)
      );
      float totalHeight = (float) rowHeights.stream().mapToDouble(f -> f).sum() + frameSize * 2;
      float totalWidth = (float) columnWidths.stream().mapToDouble(f -> f).sum() + frameSize * 2;

      if (backgroundColor != null) {
        panel.addComponents(
            rect(totalWidth, totalHeight),
            ColorComponent.color(backgroundColor)
        );
      }

      if (frameColor != null) {
        panel.add(
            rect(totalWidth - frameSize, totalHeight - frameSize).fill(false),
            pos(frameSize / 2, frameSize / 2),
            ColorComponent.color(frameColor),
            anchor(AnchorType.TOP_LEFT)
        );
      }

      for (int row = 0; row < rows.size(); row++) {
        RowBuilder rowBuilder = rows.get(row);
        GameObject rowObject = rowBuilder.buildGameObject(this);
        rowObject.set("pos", new Vec2(frameSize, rowPos + frameSize));
        panel.addChild(rowObject);

        rowPos += rowHeights.get(row);
      }

      return panel;
    }
  }

  public static class CellDefinition {
    /** Function to create the game object for this cell. */
    private Function<PanelBuilder, GameObject> cellBuilder;
    /** number of cells this game object should span. */
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
  }


  public static class RowBuilder {
    private final List<CellDefinition> cellDefinitions = new ArrayList<>();
    private int columnSpan = 1;
    private AnchorType align = null;
    private Color backgroundColor = null;

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
      return addCell(PanelBuilders.labelBuilder(text, null));

    }

    /**
     * add label with placeholder.
     */
    public RowBuilder label(String text, Color foregroundColor) {
      return addCell(PanelBuilders.labelBuilder(text, foregroundColor));
    }

    /** add slider */
    public RowBuilder slider(String property, float min, float max) {
      return addCell(PanelBuilders.sliderBuilder(property, min, max));
    }

    public RowBuilder skip() {
      return addCell(((panelBuilder) -> null));
    }

    public GameObject buildGameObject(PanelBuilder panelBuilder) {
      GameObject row = makeGameObject(
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
        float cellWidth = (float) panelBuilder.columnWidths.subList(column, column+cellDefinition.columnSpan)
            .stream().mapToDouble(i -> i).sum();
        GameObject gameObject = cellDefinition.getCellBuilder().apply(panelBuilder);
        if (gameObject != null) {
          AnchorType cellAnchorType = cellDefinition.anchorType;
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
            GameObject frame = makeGameObject(
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

        column += cellDefinition.columnSpan;
        columnPos += cellWidth;
      }

      return row;
    }

  }

}
