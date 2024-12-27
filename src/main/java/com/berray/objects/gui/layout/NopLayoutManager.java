package com.berray.objects.gui.layout;

import com.berray.GameObject;
import com.berray.math.Rect;
import com.berray.math.Vec2;
import com.berray.objects.gui.Container;

import java.util.List;

/** {@link LayoutManager} which does nothing. */
public class NopLayoutManager implements LayoutManager {
  @Override
  public void layoutPanel(Container panel, List<GameObject> childsToLayout, Rect destination) {
    float maxX = destination.getX() + destination.getWidth();
    float maxY = destination.getY() + destination.getHeight();
    // just make sure no component sticks out of the panel area
    for (GameObject gameObject : childsToLayout) {
      Vec2 pos = gameObject.get("pos");
      if (pos != null) {
        // check if the child sticks out to the left or top?
        Vec2 minPos = new Vec2(Math.max(destination.getX(), pos.getX()), Math.max(destination.getY(), pos.getY()));
        if (!minPos.equals(pos)) {
          pos = minPos;
          // yes. move the child so it is exactly in the borders
          gameObject.set("pos", pos);
        }
        Vec2 size = gameObject.get("size");
        // check if the child is too big so it sticks out to the right or bottom
        if (size != null) {
          float width = size.getX();
          float height = size.getY();
          float childMaxX = pos.getX() + width;
          if (childMaxX > maxX) {
            width = maxX - pos.getX();
          }
          float childMaxY = pos.getY() + height;
          if (childMaxY > maxY) {
            height = maxY - pos.getY();
          }
          // yes, it is to big. resize the child.
          if (width != size.getX() || height != size.getY()) {
            gameObject.set("size", new Vec2(width, height));
          }
        }
      }
    }


  }
}
