package com.berray;


import com.berray.event.Event;
import com.berray.event.EventListener;
import com.berray.math.Vec2;
import com.raylib.Raylib.Vector2;

import static com.berray.components.core.DebugComponent.debug;
import static com.raylib.Jaylib.*;
import static com.raylib.Raylib.Color;


public abstract class BerrayApplication {
  protected Game game;
  private int width = 800;
  private int height = 640;
  private Color background = WHITE;
  private String title = "Berry Application";

  protected boolean debug = false;
  protected int frameNo = 0;


  public BerrayApplication width(int width) {
    this.width = width;
    return this;
  }

  public int width() {
    return width;
  }

  public BerrayApplication height(int height) {
    this.height = height;
    return this;
  }

  public int height() {
    return height;
  }

  public Vec2 center() {
    return new Vec2(width / 2.0f, height / 2.0f);
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

  public void onUpdate(String tag, EventListener eventListener) {
    game.onUpdate(tag, eventListener);
  }

  public void onKeyPress(int key, EventListener eventListener) {
    game.on("keyPress", (event) -> {
      int pressedKey = event.getParameter(0);
      if (pressedKey == key) {
        eventListener.onEvent(event);
      }
    });
  }

  public void onKeyDown(int key, EventListener eventListener) {
    game.on("keyDown", (event) -> {
      int pressedKey = event.getParameter(0);
      if (pressedKey == key) {
        eventListener.onEvent(event);
      }
    });
  }


  public void trigger(String event, Object... params) {
    game.trigger(event, params);
  }


  public abstract void game();

  public abstract void initWindow();


  public void runGame() {
    initWindow();
    InitWindow(width, height, title);
    // Note: when the window is not ready, the GetRenderHeight() might return 0. This way the components which
    // depends on the actual window size may be wrong initialized. So wait until the window has a height > 0
    while (!IsWindowReady() || GetRenderHeight() == 0) {
      // Wait until the window is ready
      // TODO: this may be a good place to start loading assets
      Thread.yield();
    }

    this.game = new Game();
    game.on("add", this::addDebugInfos);

    game();

    SetTargetFPS(60);
    while (!WindowShouldClose()) {
      frameNo++;
      game.updateCollisions();
      game.update();
      BeginDrawing();
      {
        ClearBackground(background);
        game.draw();
        processInputs();
      }
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
    addDebugInfos(gameObject);
  }

  private static void addDebugInfos(GameObject gameObject) {
    // if the game object is already a debug object, ignore it
    if (gameObject.is("debug")) {
      return;
    }
    // recursively add debug infos
    for (GameObject child : gameObject.getChildren()) {
      addDebugInfos(child);
    }

    // only add debug infos when the object has a size
    if (gameObject.get("size") != null) {
      gameObject.add(
          debug()
      );
    }
  }

  // KEY_KB_MENU is the last key code with id 348
  private int[] keysDown = new int[KEY_KB_MENU];

  private void processInputs() {
    if (IsMouseButtonPressed(MOUSE_BUTTON_LEFT)) {
      Vector2 pos = GetMousePosition();
      game.trigger("mousePress", new Vec2(pos.x(), pos.y()));
    }

    while (true) {
      int keyCode = GetKeyPressed();
      // no more keys
      if (keyCode == 0) {
        break;
      }
      // save frame in which the key was last down.
      keysDown[keyCode] = frameNo;
    }

    for (int i = 0; i < keysDown.length; i++) {
      int frame = keysDown[i];
      if (frame > 0) {
        // key was pressed this or the last frame
        if (frame < frameNo) {
          // frame was pressed the last frame, but not this. So the key must be released
          keysDown[i] = 0;
          trigger("keyPress", i);
        } else {
          // key is still pressed (or pressed the first time)
          trigger("keyDown", i);
        }
      }
    }
  }

  public void play(String name) {
    // TODO: play sound
  }

  public void destroy(GameObject gameObject) {
    game.destroy(gameObject);
  }

}