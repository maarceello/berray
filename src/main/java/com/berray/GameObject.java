package com.berray;

import static com.raylib.Jaylib.RAYWHITE;
import static com.raylib.Raylib.DrawTextureV;

import com.berray.components.Component;
import com.berray.components.PosComponent;
import com.berray.components.SpriteComponent;
import com.raylib.Jaylib;
import com.raylib.Raylib;


import java.util.LinkedHashMap;
import java.util.Map;

public class GameObject {
  int id;
  private final Map<Class<?>,Component> components;

  public GameObject(int id) {
    this.id = id;
    this.components = new LinkedHashMap<>();
  }

  public void addComponent(Component component) {
    this.components.put(component.getClass(), component);
  }

  public void update() {
  }

  public void draw() {
    for (Component c : components.values()) {
      c.draw(this);
    }
  }

  public <E extends Component> E getComponent(Class<E> type) {
    // TODO: Refactor me please in simple
//    return (E) components.values().stream().filter(
//        component -> type.isInstance(component)
//    ).findFirst().orElse(null);

    return (E) components.get(type);

  }



}
