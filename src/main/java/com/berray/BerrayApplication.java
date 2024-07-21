package com.berray;


import com.berray.event.Event;
import com.berray.event.EventListener;
import com.berray.math.Rect;
import com.berray.math.Vec2;

import static com.berray.components.DebugComponent.debug;
import static com.raylib.Jaylib.*;
import static com.raylib.Raylib.Color;


public abstract class BerrayApplication {
  private Game game;
  private int width = 800;
  private int height = 640;
  private Color background = WHITE;
  private String title = "Berry Application";

  protected boolean debug = false;


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

  public GameObject add(Object... component) {
    return game.add(component);
  }

  public void on(String event, EventListener listener) {
    game.on(event, listener);
  }


  public void trigger(String event, Object... params) {
    game.trigger(event, params);
  }


  public abstract void initGame();

  public abstract void initWindow();


  public void runGame() {
    initWindow();
    InitWindow(width, height, title);

    this.game = new Game();
    game.on("add", this::addDebugInfos);

    initGame();

    SetTargetFPS(60);
    while (!WindowShouldClose()) {
      game.update();
      BeginDrawing();
      ClearBackground(background);
      game.draw();
      processInputs();
      EndDrawing();
    }
    CloseWindow();
  }

  /**
   * Event callback which adds debug Infos to game objects.
   */
  private void addDebugInfos(Event event) {
    if (!debug) {
      return;
    }
    GameObject gameObject = event.getParameter(1);
    // if the game object is already a debug object, ignore it
    if (gameObject.is("debug")) {
      return;
    }
    // add frame around the object
    Rect area = gameObject.get("worldArea");
    if (area != null) {
      gameObject.add(
          debug()
      );
    }
  }

  private void processInputs() {
    if (IsMouseButtonPressed(MOUSE_BUTTON_LEFT)) {
      com.raylib.Raylib.Vector2 pos = GetMousePosition();
      game.trigger("mousePress", new Vec2(pos.x(), pos.y()));
    }
  }
}