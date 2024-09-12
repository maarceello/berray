package com.berray.components;

import com.berray.Property;
import com.berray.components.core.*;
import com.berray.math.Color;
import com.berray.math.Rect;
import com.berray.math.Vec2;

public interface CoreComponentShortcuts {

  default PosComponent pos(float x, float y) {
    return PosComponent.pos(x, y);
  }

  default PosComponent pos(Vec2 pos) {
    return PosComponent.pos(pos);
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

  default TileComponent tile() {
    return TileComponent.tile();
  }

  default MouseComponent mouse() {
    return MouseComponent.mouse();
  }

  default <E> Property<E> property(String name, E value) {
    return Property.property(name, value);
  }
}
