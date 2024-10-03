package com.berray.objects.gui.panel;

import com.berray.GameObject;
import com.berray.components.core.AnchorType;
import com.berray.components.core.ColorComponent;
import com.berray.math.Color;
import com.berray.math.Vec2;
import com.berray.objects.gui.EventListenerCapable;
import com.berray.objects.gui.GameObjectBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.berray.components.core.AnchorComponent.anchor;
import static com.berray.components.core.PosComponent2d.pos;
import static com.berray.components.core.RectComponent.rect;

public class PanelBuilder implements GameObjectBuilder {
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

  public PanelBuilder() {
    this.columnWidths = Collections.emptyList();
  }

  public PanelBuilder columnWidths(Float... columnWidths) {
    this.columnWidths = Arrays.asList(columnWidths);
    return this;
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

  public PanelBuilder row(RowBuilder builder) {
    if (builder.getCellDefinitions().size() > columnWidths.size()) {
      throw new IllegalStateException("too many game objects for row: there are " + columnWidths.size() + " columns, but " + builder.getCellDefinitions().size() + " gui elements should be placed.");
    }
    rowHeights.add(builder.getHeight());
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
    GameObject panel = GameObject.makeGameObject(
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

  public static PanelBuilder makePanel() {
    return new PanelBuilder();
  }
}
