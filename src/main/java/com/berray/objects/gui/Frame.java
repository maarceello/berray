package com.berray.objects.gui;

import com.berray.GameObject;
import com.berray.components.core.*;
import com.berray.math.Color;
import com.berray.math.Rect;
import com.berray.math.Vec2;
import com.berray.objects.gui.layout.LayoutManager;

import java.util.List;

import static com.berray.components.core.AnchorComponent.anchor;
import static com.berray.components.core.PosComponent2d.pos;

public class Frame extends Panel {

  private GameObject titleBar;
  private Panel contentPane;

  public Frame(Vec2 size, LayoutManager layoutManager) {
    super(size, null);
    setLayoutManager(new FrameLayout());
    titleBar = add(
        TextComponent.text("title"),
        pos(0,0),
        anchor(AnchorType.TOP),
        ColorComponent.color(Color.GREEN),
        "titlebar"
    );
    contentPane = add(new Panel(size, layoutManager), pos(0,0), "contentPane");
  }

  @Override
  public GameObject addChild(GameObject child) {
    layoutDirty = true;
    if (contentPane != null) {
      return contentPane.addChild(child);
    }
    return super.addChild(child);
  }

  @Override
  public GameObject replaceChild(int index, GameObject other) {
    if (contentPane != null) {
      contentPane.replaceChild(index, other);
    }
    return super.replaceChild(index, other);
  }

  @Override
  public void remove(GameObject gameObject) {
    if (contentPane != null) {
      contentPane.remove(gameObject);
    }
    super.remove(gameObject);
  }

  private class FrameLayout implements LayoutManager {

    @Override
    public void layoutPanel(Container panel, List<GameObject> componentsToLayout, Rect destination) {
      // ignore componentsToLayout. Just lay out the title and the content pane
      float top = destination.getY();
      if (titleBar != null) {
        Vec2 size = titleBar.get("size");
        titleBar.set("pos", new Vec2(destination.getX() + destination.getWidth() / 2, destination.getY()));
        top += size.getY();
      }
      contentPane.set("pos", new Vec2(destination.getX(), top));
      contentPane.set("size", new Vec2(destination.getWidth(), (destination.getHeight()+destination.getX())-top));
    }
  }
}
