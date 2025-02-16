package com.berray.objects.gui.layout;

import com.berray.Game;
import com.berray.math.Rect;
import com.berray.math.Vec2;
import com.berray.objects.gui.GuiPanelReader;
import com.berray.objects.gui.Panel;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

class RowLayoutTest {

  @Test
  void testLayout() {
    Game game = new Game();
    GuiPanelReader reader = new GuiPanelReader();
    Panel gui = reader.read(getClass().getResourceAsStream("/row-layout-test.yaml"));
    gui.setGame(game);

    gui.getLayoutManager().layoutPanel(gui, gui.getChildren(), new Rect(Vec2.origin(), gui.get("size")));

    System.out.println("sizes: "+gui.getChildren().stream().map(
        gameObject -> gameObject.<Vec2>get("size")
    ).collect(Collectors.toList()));


    System.out.println("pos: "+gui.getChildren().stream().map(
        gameObject -> gameObject.<Vec2>get("pos")
    ).collect(Collectors.toList()));

  }

}