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

  private float value;


  private GameObject leftBorder;
  private GameObject rightBorder;
  private GameObject leftBar;
  private GameObject rightBar;
  private GameObject handle;

  private float leftInset;
  private float rightInset;



  public Slider(Vec2 size, float min, float max, float value) {
    this.size = size;
    this.min = min;
    this.max = max;
    this.value = value;
    on("add", this::onAdd);
    on("mouseClick", this::onMouseClick);
    on("dragging", this::onMouseDragging);
    registerProperty("size", () -> size, newSize -> this.size = newSize);
    registerProperty("value", () -> value, this::setValue);
    registerPropertyGetter("render", () -> true);
  }

  public Slider leftBorder(GameObject object) {
    this.leftBorder = object;
    return this;
  }

  public Slider rightBorder(GameObject object) {
    this.rightBorder = object;
    return this;
  }

  public Slider leftBar(GameObject object) {
    this.leftBar = object;
    return this;
  }

  public Slider rightBar(GameObject object) {
    this.rightBar = object;
    return this;
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

    updateChildPositions();
  }

  private void updateChildPositions() {
    if (leftBorder != null) {
      leftBorder.set("pos", Vec2.origin());
      leftBorder.set("anchor", AnchorType.TOP_LEFT);
    }

    if (rightBorder != null) {
      rightBorder.set("pos", new Vec2(size.getX(), 0));
      rightBorder.set("anchor", AnchorType.TOP_RIGHT);
    }

    float width = size.getX() - leftInset - rightInset;

    leftBar.set("pos", new Vec2(leftInset, 0));
    leftBar.set("size", new Vec2(width * value / (max - min), size.getY()));
    leftBar.set("anchor", AnchorType.TOP_LEFT);

    rightBar.set("size", new Vec2(width * (1 - value / (max - min)), size.getY()));
    rightBar.set("pos", new Vec2(leftInset + width * (value / (max - min)),0));
    rightBar.set("anchor", AnchorType.TOP_LEFT);

    if (handle != null) {
      handle.set("pos", new Vec2(leftInset + width * (value / (max - min)), size.getY() / 2.0f));
    }
  }

  private void onAdd(Event event) {
    GameObject parent = event.getParameter(0);
    // ignore add event when we're the one the child is added to
    if (parent != this) {
      leftInset = 0;
      rightInset = 0;
      if (leftBorder != null) {
        add(leftBorder);
        leftInset = leftBorder.<Vec2>get("size").getX();
      }
      if (rightBorder != null) {
        add(rightBorder);
        rightInset = rightBorder.<Vec2>get("size").getX();
      }
      if (leftBar != null) {
        add(leftBar);
      }
      if (rightBar != null) {
        add(rightBar);
      }
      handle = add(
          rect(size.getY() * 1.2f, size.getY() * 1.2f),
          pos(size.getX() * (value / (max - min)),size.getY() / 2.0f),
          anchor(AnchorType.CENTER),
          color(Color.WHITE)
      );

      updateChildPositions();
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
