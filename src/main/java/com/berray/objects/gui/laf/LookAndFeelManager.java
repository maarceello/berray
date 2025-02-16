package com.berray.objects.gui.laf;


import com.berray.GameObject;
import com.berray.math.Insets;
import com.berray.objects.gui.Button;
import com.berray.objects.gui.Container;
import com.berray.objects.gui.Slider;

/** Provides a Look and Feel for the panels. */
public interface LookAndFeelManager {
  /** Draws a border around the game object. */
  void drawBorder(GameObject gameObject, String border);
  /** Draws a border around the game object. */
  Insets getBorderInsets(GameObject gameObject, String border);
  /** Returns the font size. Type is a layout specific identifier what font size should be returned, ie "default", "title", etc. */
  float getFontSize(String type);


  /** Installs the look and feel to the button. */
  void installToButton(Button button);

  void clearBackground(Container container);

  void installToSlider(Slider slider);
}
