package com.berray.components;

import com.berray.GameObject;
import com.berray.Property;
import com.berray.components.core.*;
import com.berray.math.Color;
import com.berray.math.Rect;
import com.berray.math.Vec2;
import com.berray.math.Vec3;

public interface CoreComponentShortcuts {

  default GameObject make(Object... components) {
    return GameObject.makeGameObject(components);
  }

  default <E extends GameObject> E make(E gameObject, Object... components) {
    return GameObject.makeGameObject(gameObject, components);
  }

  default PosComponent2d pos(float x, float y) {
    return PosComponent2d.pos(x, y);
  }

  default PosComponent2d pos(Vec2 pos) {
    return PosComponent2d.pos(pos);
  }

  default RectComponent rect(float width, float height) {
    return RectComponent.rect(width, height);
  }

  default CircleComponent circle(float radius) {
    return CircleComponent.circle(radius);
  }

  default AnchorComponent anchor(AnchorType anchorType) {
    return AnchorComponent.anchor(anchorType);
  }

  default RotateComponent rotate(float angle) {
    return RotateComponent.rotate(angle);
  }

  default AreaComponent area() {
    return AreaComponent.area();
  }

  default AreaComponent area(Rect shape) {
    return AreaComponent.area(shape);
  }

  default TextComponent text(String text) {
    return TextComponent.text(text);
  }

  default ColorComponent color(int r, int g, int b) {
    return ColorComponent.color(r, g, b);
  }

  default ColorComponent color(Color color) {
    return ColorComponent.color(color);
  }


  default SpriteComponent sprite(String spriteId) {
    return SpriteComponent.sprite(spriteId);
  }

  default BodyComponent body() {
    return BodyComponent.body(false);
  }

  default BodyComponent body(boolean isStatic) {
    return BodyComponent.body(isStatic);
  }

  default LayerComponent layer(String layer) {
    return LayerComponent.layer(layer);
  }

  default ScaleComponent scale(float scale) {
    return ScaleComponent.scale(scale);
  }

  default ScaleComponent scale(float scaleX, float scaleY, float scaleZ) {
    return ScaleComponent.scale(scaleX, scaleY, scaleZ);
  }

  default TileComponent tile() {
    return TileComponent.tile();
  }

  default MouseComponent mouse() {
    return MouseComponent.mouse();
  }

  default <E> Property<E> property(String name, E value) {
    return Property.property(name, value);
  }

  // 3d components
  default PosComponent3d pos(float x, float y, float z) {
    return PosComponent3d.pos(x, y, z);
  }

  default PosComponent3d pos(Vec3 pos) {
    return PosComponent3d.pos(pos);
  }

}
