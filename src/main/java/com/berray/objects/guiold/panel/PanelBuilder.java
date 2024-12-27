package com.berray.objects.guiold.panel;

import com.berray.GameObject;
import com.berray.components.core.AnchorType;
import com.berray.components.core.ColorComponent;
import com.berray.event.CoreEvents;
import com.berray.math.Color;
import com.berray.math.Vec2;
import com.berray.objects.guiold.Button;
import com.berray.objects.guiold.EventListenerCapable;
import com.berray.objects.guiold.GameObjectBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.berray.components.core.AnchorComponent.anchor;
import static com.berray.components.core.AreaComponent.area;
import static com.berray.components.core.MouseComponent.mouse;
import static com.berray.components.core.PosComponent2d.pos;
import static com.berray.components.core.RectComponent.rect;
import static com.berray.components.core.TextComponent.text;

public class PanelBuilder implements GameObjectBuilder {
  public List<Float> columnWidths;
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

  /**
   * Size of the font for the title bar and the default size for the panel.
   */
  private int fontSize = 20;

  /**
   * Title for the panel. default: <code>null</code> for no title.
   */
  private String title = null;
  /**
   * true when the panel can be dragged by the titlebar, false when it should be static.
   */
  private boolean moveable = false;
  /**
   * true when the panel can be minimized (only the title bar is shown).
   */
  private boolean minimizable = false;

  public PanelBuilder() {
    this.columnWidths = Collections.emptyList();
  }

  public PanelBuilder title(String title) {
    this.title = title;
    return this;
  }

  public PanelBuilder fontSize(int fontSize) {
    this.fontSize = fontSize;
    return this;
  }


  public PanelBuilder movable(boolean moveable) {
    this.moveable = moveable;
    // movable panels  needs a title bar
    if (title == null) {
      title = "";
    }
    return this;
  }

  public PanelBuilder minimizable(boolean minimizable) {
    this.minimizable = minimizable;
    // minimizable panels  needs a title bar
    if (title == null) {
      title = "";
    }
    return this;
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
    GameObject panel = GameObject.makeGameObject(
        pos(0, 0),
        anchor(AnchorType.TOP_LEFT),
        "panel"
    );
    float totalWidth = (float) columnWidths.stream().mapToDouble(f -> f).sum() + frameSize * 2;

    int titlebarHeight = 0;
    if (title != null) {
      GameObject titleRow = createTitleRow(panel, totalWidth);
      panel.add(titleRow);
      titlebarHeight += titleRow.<Vec2>get("size").getY() + 1;
    }

    GameObject content = panel.add(pos(0, titlebarHeight), "content");
    int rowPos = 0;
    for (int row = 0; row < rows.size(); row++) {
      RowBuilder rowBuilder = rows.get(row);
      GameObject rowObject = rowBuilder.buildGameObject(this);
      rowObject.set("pos", new Vec2(frameSize, rowPos + frameSize));
      content.addChild(rowObject);

      rowPos += rowHeights.get(row);
    }

    if (backgroundColor != null) {
      panel.addComponents(
          rect(totalWidth, titlebarHeight + rowPos + frameSize * 2),
          ColorComponent.color(backgroundColor),
          "background"
      );
    }

    if (frameColor != null) {
      panel.add(
          rect(totalWidth - frameSize, titlebarHeight + rowPos + frameSize).fill(false),
          pos(frameSize / 2, frameSize / 2),
          ColorComponent.color(frameColor),
          anchor(AnchorType.TOP_LEFT),
          "frame"
      );
    }

    panel.setProperty("minimizedState", false);

    float finalTitlebarHeight = titlebarHeight;
    float finalRowPos = rowPos;
    panel.registerAction("toggleMinimize", () -> {
      Boolean state = panel.getProperty("minimizedState");
      if (Boolean.TRUE == state) {
        // restore state
        content.setPaused(false);
        panel.getChildren("frame").forEach(gameObject -> gameObject.set("size", new Vec2(totalWidth - frameSize, finalTitlebarHeight + finalRowPos + frameSize)));
        panel.set("size", new Vec2(totalWidth, finalTitlebarHeight + finalRowPos + frameSize * 2));
      } else {
        // minimize panel
        content.setPaused(true);
        panel.getChildren("frame").forEach(gameObject -> gameObject.set("size", new Vec2(totalWidth - frameSize, finalTitlebarHeight + frameSize)));
        panel.set("size", new Vec2(totalWidth, finalTitlebarHeight + frameSize * 2));
      }
      panel.setProperty("minimizedState", Boolean.TRUE != state);
    });


    return panel;
  }

  private GameObject createTitleRow(GameObject panel, float width) {

    GameObject titlebarRow = GameObject.makeGameObject(
        pos(frameSize, frameSize),
        ColorComponent.color(foregroundColor),
        anchor(AnchorType.TOP_LEFT),
        "titlebar"
    );

    float textWidth = width - frameSize * 2;
    // do we need space for the minimize icon?
    if (minimizable) {
      // yes. the icon is square, so reserve the same width as the height
      textWidth -= fontSize;

      GameObject minimizeButton = titlebarRow.add(
          new Button("minimize", false)
              .neutral(
                  rect(fontSize - 4.0f, fontSize - 4.0f).fill(false).lineThickness(2),
                  ColorComponent.color(backgroundColor == null ? foregroundColor : backgroundColor),
                  anchor(AnchorType.TOP_LEFT)
              ),
          pos(textWidth + 2, 2),
          anchor(AnchorType.TOP_LEFT),
          "minimizeButton"
      );
      minimizeButton.on("click", event -> titlebarRow.getParent().doAction("toggleMinimize"));
    }

    // set text
    GameObject titleText = titlebarRow.add(
        text(title),
        pos(textWidth / 2, 0),
        anchor(AnchorType.TOP),
        ColorComponent.color(backgroundColor == null ? foregroundColor : backgroundColor)
    );
    titleText.set("fontHeight", fontSize);

    if (backgroundColor != null) {
      titlebarRow.addComponents(
          rect(width - frameSize * 2, fontSize).fill(true)
      );
    }

    if (moveable) {
      titlebarRow.addComponents(
          mouse(),
          area()
      );

      PanelDragManager dragManager = new PanelDragManager(panel);
      titlebarRow.on(CoreEvents.DRAG_START, dragManager::dragStart);
      titlebarRow.on(CoreEvents.DRAGGING, dragManager::dragUpdate);
      titlebarRow.on(CoreEvents.DRAG_FINISH, dragManager::dragFinish);
    }

    return titlebarRow;
  }

  public static PanelBuilder makePanel() {
    return new PanelBuilder();
  }
}
