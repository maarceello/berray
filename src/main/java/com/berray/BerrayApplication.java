package com.berray;

import static com.raylib.Jaylib.RAYWHITE;
import static com.raylib.Raylib.*;

import com.berray.components.Component;

public abstract class BerrayApplication {
  private Game game;

  public void add(Component... component) {
    game.add(component);
  }

  public abstract void init();

  public void run() {
    int screenWidth = 800;
    int screenHeight = 450;
    InitWindow(screenWidth, screenHeight, "Raylib Java");

    this.game = new Game();
    init();

    SetTargetFPS(60);
    while (!WindowShouldClose()) {
      game.update();
      BeginDrawing();
      ClearBackground(RAYWHITE);
      game.draw();
      EndDrawing();
    }
    CloseWindow();
  }
}