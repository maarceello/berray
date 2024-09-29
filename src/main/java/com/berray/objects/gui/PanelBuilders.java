package com.berray.objects.gui;

import com.berray.GameObject;
import com.berray.components.core.AnchorType;
import com.berray.event.PropertyChangeEvent;
import com.berray.math.Color;
import com.berray.math.Vec2;

import java.util.function.Function;

import static com.berray.GameObject.makeGameObject;
import static com.berray.components.core.AnchorComponent.anchor;
import static com.berray.components.core.ColorComponent.color;
import static com.berray.components.core.RectComponent.rect;
import static com.berray.components.core.TextComponent.text;

/** Default panel builders */
public class PanelBuilders {

  public static Function<Panel.PanelBuilder, GameObject> labelBuilder(String text, Color foregroundColor) {
    return (Panel.PanelBuilder panelBuilder) -> {
      Color color = foregroundColor != null ? foregroundColor : panelBuilder.getForegroundColor();
      EventListenerCapable dataObject = panelBuilder.getDataObject();
      GameObject label = makeGameObject(
          text(GuiService.replaceText(text, dataObject)),
          color(color),
          anchor(AnchorType.TOP_LEFT)
      );
      if (dataObject != null) {
        dataObject.onPropertyChange(event -> label.set("text", GuiService.replaceText(text, dataObject)));
      }
      return label;
    };
  }

  public static  Function<Panel.PanelBuilder, GameObject> sliderBuilder(String property, float min, float max) {
    return (panelBuilder) -> {
      Color foregroundColor = panelBuilder.getForegroundColor();
      EventListenerCapable dataObject = panelBuilder.getDataObject();
      GameObject slider = makeGameObject(
          new Slider(new Vec2(1.0f, 1.0f), min, max, min)
              .leftBar(
                  rect(1.0f, 1.0f).fill(false),
                  color(foregroundColor)
              )
              .rightBar(
                  rect(1.0f, 1.0f).fill(false),
                  color(foregroundColor)
              ).handle(
                  rect(10.0f, 20).fill(true),
                  color(foregroundColor)
              )
      );
      if (dataObject != null) {
        dataObject.onPropertyChange((PropertyChangeEvent event) -> {
          if (event.getPropertyName().equals(property)) {
            slider.set("value", event.getNewValue());
          }
        });

        slider.on("propertyChange", (PropertyChangeEvent event) -> {
          if (event.getPropertyName().equals("value")) {
            dataObject.setProperty(property, event.getNewValue());
          }
        });
      }
      return slider;
    };
  }


}
