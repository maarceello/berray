package com.berray.objects.gui.laf;


import com.berray.GameObject;
import com.berray.math.Insets;
import com.berray.objects.gui.Button;
import com.berray.objects.gui.Slider;

/** Provides a Look and Feel for the panels. */
public interface LookAndFeelManager {
  /** Draws a border around the game object. */
  void drawBorder(GameObject gameObject, String border);
  /** Draws a border around the game object. */
  Insets getBorderInsets(GameObject gameObject, String border);


  /** Installs the look and feel to the button. */
  void installToButton(Button button);

  void clearBackground(Button button);

  void installToSlider(Slider slider);
}
