package com.berray;


import static com.raylib.Jaylib.*;
import static com.raylib.Raylib.Color;


import com.berray.components.Component;


public abstract class BerrayApplication {
  private Game game;
  private int width = 800;
  private int height = 640;
  private Color background = WHITE;
  private String title = "Berry Application";


  public BerrayApplication width(int width) {
    this.width = width;
    return this;
  }

  public BerrayApplication height(int height) {
    this.height = height;
    return this;
  }

  public BerrayApplication title(String title) {
    this.title = title;
    return this;
  }

  // TODO: Accept a Ray Color or an Array [r, g, b, a]
  public BerrayApplication background(Color background) {
    this.background = background;
    return this;
  }


  public GameObject add(Component... component) {
    return game.add(component);
  }

  public abstract void initGame();

  public abstract void initWindow();


  public void runGame() {
    initWindow();
    InitWindow(width, height, title);

    this.game = new Game();

    initGame();

    SetTargetFPS(60);
    while (!WindowShouldClose()) {
      game.update();
      BeginDrawing();
      ClearBackground(background);
      game.draw();
      EndDrawing();
    }
    CloseWindow();
  }
}