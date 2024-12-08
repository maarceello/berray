package com.berray.objects.gui.layout;

import com.berray.GameObject;
import com.berray.math.Rect;
import com.berray.objects.gui.Container;
import com.berray.objects.gui.Panel;

import java.util.List;

/** Layouter able to position and resize game objects in a @link {@link Panel panel}. */
public interface LayoutManager {
  void layoutPanel(Container panel, List<GameObject> componentsToLayout, Rect destination);
}
