package com.berray.objects.guiold.panel;

import com.berray.GameObject;
import com.berray.components.core.AnchorType;
import com.berray.event.CoreEvents;
import com.berray.event.PropertyChangeEvent;
import com.berray.math.Color;
import com.berray.math.Vec2;
import com.berray.objects.guiold.Button;
import com.berray.objects.guiold.EventListenerCapable;
import com.berray.objects.guiold.PropertyResolveService;
import com.berray.objects.guiold.Slider;

import java.util.function.Function;

import static com.berray.GameObject.makeGameObject;
import static com.berray.components.core.AnchorComponent.anchor;
import static com.berray.components.core.ColorComponent.color;
import static com.berray.components.core.RectComponent.rect;
import static com.berray.components.core.TextComponent.text;
import static com.berray.objects.guiold.Button.button;

/**
 * Default panel builders
 */
public class DefaultBuilders {

  public Function<PanelBuilder, GameObject> labelBuilder(String text, Color foregroundColor) {
    return (PanelBuilder panelBuilder) -> {
      Color color = foregroundColor != null ? foregroundColor : panelBuilder.getForegroundColor();
      EventListenerCapable dataObject = panelBuilder.getDataObject();
      GameObject label = makeGameObject(
          text(PropertyResolveService.replaceText(text, dataObject)),
          color(color),
          anchor(AnchorType.TOP_LEFT)
      );
      if (dataObject != null) {
        dataObject.onPropertyChange(event -> label.set("text", PropertyResolveService.replaceText(text, dataObject)));
      }
      return label;
    };
  }

  public Function<PanelBuilder, GameObject> sliderBuilder(String property, float height, float min, float max) {
    return (panelBuilder) -> {
      Color foregroundColor = panelBuilder.getForegroundColor();
      EventListenerCapable dataObject = panelBuilder.getDataObject();
      GameObject slider = GameObject.makeGameObject(
          new Slider(new Vec2(1.0f, 1.0f), min, max, min)
              .leftBar(
                  rect(1.0f, 1.0f).fill(false),
                  color(foregroundColor)
              )
              .rightBar(
                  rect(1.0f, 1.0f).fill(false),
                  color(foregroundColor)
              ).handle(
                  rect(height / 2.0f, height).fill(true),
                  color(foregroundColor)
              )
      );
      if (dataObject != null) {
        dataObject.onPropertyChange((PropertyChangeEvent event) -> {
          if (event.getPropertyName().equals(property)) {
            slider.set("value", event.getNewValue());
          }
        });

        slider.on(CoreEvents.PROPERTY_CHANGED, (PropertyChangeEvent event) -> {
          if (event.getPropertyName().equals("value")) {
            dataObject.setProperty(property, event.getNewValue());
          }
        });
      }
      return slider;
    };
  }


  public Function<PanelBuilder, GameObject> checkboxBuilder(String property, float height) {
    return (PanelBuilder panelBuilder) -> {
      EventListenerCapable dataObject = panelBuilder.getDataObject();

      Button button = makeGameObject(
          button("testbutton", true)
              .square()
              .neutral(
                  rect(height, height).fill(false),
                  color(Color.GOLD),
                  anchor(AnchorType.TOP_LEFT)
              )
              .pressed(
                  rect(height, height).fill(true),
                  color(Color.GOLD),
                  anchor(AnchorType.TOP_LEFT)
              ),
          anchor(AnchorType.TOP_LEFT)
      );
      if (dataObject != null) {
        dataObject.onPropertyChange(event ->
            {
              if (event.getPropertyName().equals("checked")) {
                button.set("pressed", event.getNewValue());
              }
            }
        );
        button.on(CoreEvents.PROPERTY_CHANGED, (PropertyChangeEvent event) ->
        {
          if (event.getPropertyName().equals("pressed")) {
            dataObject.setProperty(property, event.getNewValue());
          }
        });
      }
      return button;
    };
  }
}
