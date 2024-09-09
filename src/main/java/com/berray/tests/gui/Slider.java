package com.berray.tests.gui;

import com.berray.GameObject;
import com.berray.components.CoreComponentShortcuts;
import com.berray.components.core.AnchorType;
import com.berray.event.Event;
import com.berray.math.Color;
import com.berray.math.Vec2;

import java.util.Arrays;
import java.util.List;

/** Test for a horizontal slider.
 *
 * Slider parts:
 * * full edge
 * * full gauge
 * * slider knob
 * * empty gauge
 * * empty edge
 * * maybe more/less buttons
 * * maybe some textual representation of the progress. The text can be
 * ** int the full gauge
 * ** in the empty gauge
 * ** in the knob
 * ** somewhere outside of the slider
 *
 * */
public class Slider extends GameObject implements CoreComponentShortcuts {

  private Vec2 size;
  private float min;
  private float max;

  private float value = 20;


  private GameObject left;
  private GameObject right;
  private GameObject knob;



  public Slider(Vec2 size, float min, float max) {
    this.size = size;
    this.min = min;
    this.max = max;
    on("add", this::onAdd);
    on("mouseClick", this::onMouseClick);
    on("dragging", this::onMouseDragging);
    registerProperty("size", () -> size, newSize -> this.size = newSize);
    registerProperty("value", () -> value, this::setValue);
    registerPropertyGetter("render", () -> true);
  }

  private void onMouseDragging(Event event) {
    Vec2 relativePos = event.getParameter(1);
    float percent = relativePos.getX() / size.getX();
    setValue(min + (max - min) * percent);
  }

  private void onMouseClick(Event event) {
    Vec2 relativePos = event.getParameter(1);
    float percent = relativePos.getX() / size.getX();
    setValue(min + (max - min) * percent);
  }

  private void setValue(float value) {
    if (value < min) {
      value = min;
    }
    if (value > max) {
      value = max;
    }
    firePropertyChange("value", this.value, value);
    this.value = value;
    left.set("size", new Vec2(size.getX() * value / (max - min), size.getY()));
    right.set("size", new Vec2(size.getX() * (1 - value / (max - min)), size.getY()));
    right.set("pos", new Vec2(size.getX() * (value / (max - min)),0));
    knob.set("pos", new Vec2(size.getX() * (value / (max - min)),size.getY() / 2.0f));
  }

  private void onAdd(Event event) {
    GameObject parent = event.getParameter(0);
    // ignore add event when we're the one the child is added to
    if (parent != this) {
      left = add(
          rect(size.getX() * value / (max - min), size.getY()),
          pos(0,0),
          anchor(AnchorType.TOP_LEFT),
          color(Color.GOLD)
      );
      right = add(
          rect(size.getX() * (1 - value / (max - min)), size.getY()),
          pos(size.getX() * (value / (max - min)),0),
          anchor(AnchorType.TOP_LEFT),
          color(Color.GRAY)
      );
      knob = add(
          rect(size.getY() * 1.2f, size.getY() * 1.2f),
          pos(size.getX() * (value / (max - min)),size.getY() / 2.0f),
          anchor(AnchorType.CENTER),
          color(Color.WHITE)
      );
    }
    setTransformDirty();
  }

  @Override
  public void addComponents(List<Object> components) {
    // first add our own components
    super.addComponents(Arrays.asList(
        area(),
        mouse(),
        pos(0, 0))
    );
    // then add the supplied components. these may overwrite our own components.
    super.addComponents(components);
  }


}
