package com.berray.objects.gui.layout;

import com.berray.GameObject;
import com.berray.math.Rect;
import com.berray.math.Vec2;
import com.berray.objects.gui.Container;

import java.util.List;

/**
 * Maximizes the childs inside the container. Multiple childs overlap each other.
 */
public class FillLayout implements LayoutManager {
  @Override
  public void layoutPanel(Container panel, List<GameObject> componentsToLayout, Rect destination) {
    Vec2 pos = destination.getPos();
    Vec2 size = destination.getSize();
    for (GameObject child : componentsToLayout) {
      child.set("pos", pos);
      if (child.isWritable("size")) {
        child.set("size", size);
      }
    }
  }
}
