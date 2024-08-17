package com.berray;


import com.berray.assets.AssetManager;
import com.berray.components.core.AnchorType;
import com.berray.event.Event;
import com.berray.event.EventListener;
import com.berray.math.Rect;
import com.berray.math.Vec2;
import com.raylib.Raylib;
import com.raylib.Raylib.Vector2;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.berray.components.core.AnchorComponent.anchor;
import static com.berray.components.core.DebugComponent.debug;
import static com.berray.components.core.LayerComponent.layer;
import static com.berray.components.core.PosComponent.pos;
import static com.berray.objects.core.Label.label;
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
  protected int targetFps = 60;

  protected Timings timings = new Timings();

  // KEY_KB_MENU is the last key code with id 348
  private int[] keysDown = new int[KEY_KB_MENU];

  private Map<String, Consumer<SceneDescription>> scenes = new HashMap<>();


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
    return game.center();
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

  public BerrayApplication layers(String... layers) {
    List<String> layerList = Arrays.asList(layers);
    if (!layerList.contains(Game.DEFAULT_LAYER)) {
      throw new IllegalStateException("layer list must contain '"+Game.DEFAULT_LAYER+"' layer");
    }
    game.setLayers(layerList);
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

  public void onKeyPress(EventListener eventListener) {
    game.on("keyPress", eventListener);
  }

  public void onKeyPress(int key, EventListener eventListener) {
    game.on("keyPress", event -> {
      int pressedKey = event.getParameter(0);
      if (pressedKey == key) {
        eventListener.onEvent(event);
      }
    });
  }

  public void onKeyDown(int key, EventListener eventListener) {
    game.on("keyDown", event -> {
      int pressedKey = event.getParameter(0);
      if (pressedKey == key) {
        eventListener.onEvent(event);
      }
    });
  }

  public void onKeyRelease(int key, EventListener eventListener) {
    game.on("keyUp", event -> {
      int pressedKey = event.getParameter(0);
      if (pressedKey == key) {
        eventListener.onEvent(event);
      }
    });
  }

  public void trigger(String event, Object... params) {
    game.trigger(event, params);
  }


  public AssetManager getAssetManager() {
    return game.getAssetManager();
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
    if (debug) {
      // add fps display
      addFpsLabel();
      // add timings display
      addTimingsLabel();
    }

    if (targetFps > 0) {
      SetTargetFPS(targetFps);
    }
    while (!WindowShouldClose()) {
      frameNo++;
      timings.timeCollisionDetection(() -> game.updateCollisions());
      timings.timeUpdate(() -> game.update(frameTime()));
      timings.timeRaylib(Raylib::BeginDrawing);
      timings.timeDraw(() -> {
        ClearBackground(background);
        game.draw();
      });
      timings.timeRaylib(Raylib::EndDrawing);
      timings.timeInput(this::processInputs);
      timings.apply();
    }
    CloseWindow();
  }

  protected void addTimingsLabel(Object... additionalComponents) {
    GameObject timingsLabel = add(label(() -> "Timings:\n" + timings()),
        pos(width(), 20),
        anchor(AnchorType.TOP_RIGHT),
        "debug");
    timingsLabel.addComponents(additionalComponents);
    if (game.getLayers().contains("gui")) {
      timingsLabel.addComponents(layer("gui"));
    }
  }

  protected void addFpsLabel(Object... additionalComponents) {
    GameObject fpsLabel = add(label(() -> "FPS: " + fps()),
        pos(width(), 0),
        anchor(AnchorType.TOP_RIGHT),
        "debug");
    fpsLabel.addComponents(additionalComponents);
    if (game.getLayers().contains("gui")) {
      fpsLabel.addComponents(layer("gui"));
    }
  }

  protected String timings() {
    return String.format("CD: %.1f%% %nUP: %.1f%%%nDR: %.1f%%%nIN: %.1f%%%nRL: %.1f%%",
        timings.getPercentCollisionDetection(),
        timings.getPercentUpdate(),
        timings.getPercentDraw(),
        timings.getPercentInput(),
        timings.getPercentRaylib()
    );
  }


  /**
   * Returns the time passed since the last frame.
   * Note: by overriding this method and returning a fixed value you can simulate a constant
   * framerate even when the application is halted during debugging or similar.
   */
  public float frameTime() {
    // TODO: return fixed framerate 1.0f/60 when debug == true?
    return Raylib.GetFrameTime();
  }

  /**
   * Returns the Frames per second.
   */
  public int fps() {
    return Raylib.GetFPS();
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

  public void addDebugInfos(GameObject gameObject) {
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
      gameObject.addComponents(
          debug()
      );
    }
  }

  private void processInputs() {
    if (IsMouseButtonPressed(MOUSE_BUTTON_LEFT)) {
      Vector2 pos = GetMousePosition();
      Vec2 mousePos = new Vec2(pos.x(), pos.y());

      game.trigger("mousePress", mousePos);

      List<GameObject> objectsWithBoundingBox = game.getRoot().getGameObjectStream()
          .filter(gameObject -> gameObject.getBoundingBox() != null)
          .collect(Collectors.toList());

      for (GameObject gameObject : objectsWithBoundingBox) {
        Rect boundingBox = gameObject.getBoundingBox();
        if (boundingBox.contains(mousePos)) {
          // todo: calculate mouse pos in object local coordinates
          gameObject.trigger("click", mousePos);
        }
      }
    }

    for (int i = 0; i < keysDown.length; i++) {
      int frame = keysDown[i];
      // check if the key is down?
      if (IsKeyDown(i)) {
        if (frame == 0) {
          // key is pressed this frame
          trigger("keyPress", i);
        }
        // key is still pressed (or pressed the first time)
        trigger("keyDown", i);
        keysDown[i] = frameNo;
      } else {
        // Key not down, but was down the previous frame? Then it was released this frame.
        if (frame != 0) {
          keysDown[i] = 0;
          trigger("keyUp", i);
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

  public int rand(int min, int maxExclusive) {
    return new Random().nextInt(maxExclusive - min) + min;
  }


  /** add scene to game. */
  public void scene(String name, Consumer<SceneDescription> sceneCreator) {
    scenes.put(name, sceneCreator);
  }


  /** remove current scene and create new named scene. */
  public void go(String scene, Object... params) {
    game.getRoot().getChildren().clear();
    game.clearEvents();
    GameObject root = game.getRoot();

    SceneDescription description = new SceneDescription() {
      @Override
      public GameObject add(Object... components) {
        return root.add(components);
      }

      @Override
      public void on(String event, EventListener listener) {
        root.on(event, listener);
      }

      @Override
      public <E> E getParameter(int paramNr) {
        return (E) params[paramNr];
      }

      @Override
      public void onKeyPress(EventListener eventListener) {
        BerrayApplication.this.onKeyPress(eventListener);
      }
    };
    scenes.get(scene).accept(description);
  }

}