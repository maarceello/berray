package com.berray;

import static com.raylib.Jaylib.RAYWHITE;
import static com.raylib.Raylib.DrawTextureV;

import com.berray.components.Component;
import com.berray.components.PosComponent;
import com.berray.components.SpriteComponent;
import com.raylib.Jaylib;
import com.raylib.Raylib;

import java.util.HashMap;
import java.util.Map;

public class GameObject {
  int id;
  private final Map<Integer, Component> components;

  public GameObject(int id) {
    this.id = id;
    this.components = new HashMap<>();
  }

  public void addComponent(Component component) {
    this.components.put(component.getType(), component);
  }

  public void update() {
  }

  public void draw() {
    PosComponent posComponent = (PosComponent) components.get(1);
    SpriteComponent spriteComponent = (SpriteComponent) components.get(2);

    if (posComponent != null && spriteComponent != null) {

      Jaylib.Vector2 pos = posComponent.getPos();
      Raylib.Texture texture = spriteComponent.getTexture();
      DrawTextureV(texture, pos, RAYWHITE);
    }
  }
}
