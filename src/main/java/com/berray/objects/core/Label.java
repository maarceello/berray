package com.berray.objects.core;

import com.berray.GameObject;
import com.berray.event.CoreEvents;

import java.util.function.Supplier;

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
        text(textSupplier.get())
    );

    on(CoreEvents.UPDATE, event -> {
      set("text", textSupplier.get());
    });
  }

  public static Label label(Supplier<String> textSupplier) {
    return new Label(textSupplier);
  }
}
