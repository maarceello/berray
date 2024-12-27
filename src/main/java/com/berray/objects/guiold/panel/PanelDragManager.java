package com.berray.objects.guiold.panel;

import com.berray.GameObject;
import com.berray.event.MouseEvent;
import com.berray.math.Vec2;

public class PanelDragManager {
  private final GameObject panel;
  private Vec2 dragStart;
  private Vec2 panelStart;

  public PanelDragManager(GameObject panel) {
    this.panel = panel;
  }

  public void dragStart(MouseEvent event) {
    this.dragStart = event.getWindowPos();
    this.panelStart = panel.get("pos");
  }

  public void dragUpdate(MouseEvent event) {
    Vec2 delta = event.getWindowPos().sub(dragStart);
    panel.set("pos", panelStart.add(delta));
  }

  public void dragFinish(MouseEvent event) {
    dragStart = null;
    panelStart = null;
  }
}
