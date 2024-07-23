package com.berray.objects;

import com.berray.GameObject;
import com.berray.components.AnchorComponent;
import com.berray.components.AnchorType;
import com.berray.components.PosComponent;
import com.berray.components.TextComponent;
import com.berray.math.Vec2;

import java.util.function.Supplier;

import static com.berray.components.AnchorType.CENTER;

/**
 * Simple text based label. The labels content is updated each frame from the supplier.
 *
 * Usage:
 *
 * ```
 * game.addChild(new Label(<pos>, <anchor>, () -> "text to be displayed"));
 * ```
 */
public class Label extends GameObject {
  public Label(Vec2 pos, AnchorType anchorType, Supplier<String> textSupplier) {
    addComponents(
        TextComponent.text(textSupplier.get()),
        PosComponent.pos(pos),
        AnchorComponent.anchor(anchorType));

    on("update", event -> {
      set("text", textSupplier.get());
    });
  }

  public Label(Vec2 pos, Supplier<String> textSupplier) {
    this(pos, CENTER, textSupplier);
  }
}
