package com.berray;


import com.berray.components.core.*;
import com.berray.event.Event;
import com.berray.event.EventListener;
import com.berray.math.Rect;
import com.berray.math.Vec2;

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
      game.checkFrame();
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
    // if the game object is already a debug object, ignore it
    if (gameObject.is("debug")) {
      return;
    }
    // add frame around the object
    Rect area = gameObject.get("localArea");
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

  // Shortcuts to some common base components
  public PosComponent pos(float x, float y) {
    return PosComponent.pos(x, y);
  }

  public PosComponent pos(Vec2 pos) {
    return PosComponent.pos(pos);
  }

  public static RectComponent rect(float width, float height) {
    return RectComponent.rect(width, height);
  }

  public static CircleComponent circle(float radius) {
    return CircleComponent.circle(radius);
  }

  public static AnchorComponent anchor(AnchorType anchorType) {
    return AnchorComponent.anchor(anchorType);
  }

  public static RotateComponent rotate(float angle) {
    return RotateComponent.rotate(angle);
  }

  public static AreaComponent area() {
    return AreaComponent.area();
  }

  public static AreaComponent area(Rect shape) {
    return AreaComponent.area(shape);
  }

  public TextComponent text(String text) {
    return TextComponent.text(text);
  }

  public ColorComponent color(int r, int g, int b) {
    return ColorComponent.color(r, g, b);
  }


}