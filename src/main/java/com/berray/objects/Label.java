package com.berray.objects;

import com.berray.GameObject;
import com.berray.components.core.AnchorComponent;
import com.berray.components.core.AnchorType;
import com.berray.components.core.PosComponent;
import com.berray.math.Vec2;

import java.util.Arrays;
import java.util.function.Supplier;

import static com.berray.components.core.AnchorType.CENTER;
import static com.berray.components.core.TextComponent.text;

/**
 * Simple text based label. The labels content is updated each frame from the supplier.
 * <p>
 * Usage:
 * <p>
 * ```
 * game.addChild(new Label(<pos>, <anchor>, () -> "text to be displayed"));
 * ```
 */
public class Label extends GameObject {
  public Label(Supplier<String> textSupplier) {
    addComponents(
        Arrays.asList(text(textSupplier.get()))
    );

    on("update", event -> {
      set("text", textSupplier.get());
    });
  }

  public Label(Vec2 pos, AnchorType anchorType, Supplier<String> textSupplier) {
    addComponents(
        Arrays.asList(
            text(textSupplier.get()),
            PosComponent.pos(pos),
            AnchorComponent.anchor(anchorType)));

    on("update", event -> {
      set("text", textSupplier.get());
    });
  }

  public Label(Vec2 pos, Supplier<String> textSupplier) {
    this(pos, CENTER, textSupplier);
  }

  public static Label label(Supplier<String> textSupplier) {
    return new Label(textSupplier);
  }
}
