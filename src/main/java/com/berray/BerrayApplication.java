package com.berray;


import com.berray.assets.DefaultAssetManager;
import com.berray.components.core.AnchorType;
import com.berray.event.*;
import com.berray.event.EventListener;
import com.berray.math.Color;
import com.berray.math.Vec2;
import com.raylib.Raylib;
import com.raylib.Raylib.Vector2;

import java.util.*;
import java.util.function.Consumer;

import static com.berray.components.core.AnchorComponent.anchor;
import static com.berray.components.core.DebugComponent.debug;
import static com.berray.components.core.LayerComponent.layer;
import static com.berray.components.core.PosComponent2d.pos;
import static com.berray.objects.core.Label.label;
import static com.raylib.Jaylib.*;


public abstract class BerrayApplication {
  protected Game game;
  private int width = 800;
  private int height = 640;
  private Color background = Color.WHITE;
  private String title = "Berry Application";

  protected boolean debug = false;
  protected int frameNo = 0;
  protected int targetFps = 60;

  protected Timings timings = new Timings();

  // KEY_KB_MENU is the last key code with id 348
  private final int[] keysDown = new int[KEY_KB_MENU];

  private final Map<String, Consumer<SceneDescription>> scenes = new HashMap<>();
  private final Random random = new Random();


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

  public BerrayApplication background(Color background) {
    this.background = background;
    return this;
  }

  public BerrayApplication layers(String... layers) {
    List<String> layerList = Arrays.asList(layers);
    if (!layerList.contains(Game.DEFAULT_LAYER)) {
      throw new IllegalStateException("layer list must contain '" + Game.DEFAULT_LAYER + "' layer");
    }
    game.setLayers(layerList);
    return this;
  }

  public GameObject add(Object... component) {
    return game.add(component);
  }

  public <E extends GameObject> E add(E gameObject, Object... component) {
    return game.add(gameObject, component);
  }


  public <E extends Event> void on(String event, EventListener<E> listener) {
    game.on(event, listener);
  }

  public void onUpdate(String tag, EventListener<UpdateEvent> eventListener) {
    game.onUpdate(tag, eventListener);
  }

  public void onKeyPress(EventListener<KeyEvent> eventListener) {
    game.on(CoreEvents.KEY_PRESS, eventListener);
  }

  public void onKeyPress(int key, EventListener<KeyEvent> eventListener) {
    game.on(CoreEvents.KEY_PRESS, (KeyEvent event) -> {
      int pressedKey = event.getKeyCode();
      if (pressedKey == key) {
        eventListener.onEvent(event);
      }
    });
  }

  public void onKeyDown(int key, EventListener<KeyEvent> eventListener) {
    game.on(CoreEvents.KEY_DOWN, (KeyEvent event) -> {
      int pressedKey = event.getKeyCode();
      if (pressedKey == key) {
        eventListener.onEvent(event);
      }
    });
  }

  public void onKeyRelease(int key, EventListener<KeyEvent> eventListener) {
    game.on(CoreEvents.KEY_UP, (KeyEvent event) -> {
      int pressedKey = event.getKeyCode();
      if (pressedKey == key) {
        eventListener.onEvent(event);
      }
    });
  }

  public void trigger(String event, Object... params) {
    game.trigger(event, params);
  }


  public DefaultAssetManager getAssetManager() {
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
    game.on(CoreEvents.ADD, this::onAddDebugInfos);

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
        ClearBackground(background.toRaylibColor());
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
  private void onAddDebugInfos(AddEvent event) {
    if (!debug) {
      return;
    }
    GameObject gameObject = event.getChild();
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
    Vector2 mousePosRaylib = GetMousePosition();
    Vec2 currentMousePos = new Vec2(mousePosRaylib.x(), mousePosRaylib.y());

    game.trigger(CoreEvents.MOUSE_MOVE, null, currentMousePos);

    if (IsMouseButtonPressed(MOUSE_BUTTON_LEFT) || IsMouseButtonPressed(MOUSE_BUTTON_RIGHT)  || IsMouseButtonPressed(MOUSE_BUTTON_MIDDLE)) {
      game.trigger(CoreEvents.MOUSE_PRESS, null, currentMousePos);
    }
    if (IsMouseButtonReleased(MOUSE_BUTTON_LEFT) || IsMouseButtonReleased(MOUSE_BUTTON_RIGHT)  || IsMouseButtonReleased(MOUSE_BUTTON_MIDDLE)) {
      game.trigger(CoreEvents.MOUSE_RELEASE, null, currentMousePos);
    }
    float mouseWheeelMove = Raylib.GetMouseWheelMove();
    if (mouseWheeelMove != 0.0f) {
      game.trigger(CoreEvents.MOUSE_WHEEL_MOVE, null, currentMousePos, null, mouseWheeelMove);
    }

    for (int i = 0; i < keysDown.length; i++) {
      int frame = keysDown[i];
      // check if the key is down?
      if (IsKeyDown(i)) {
        if (frame == 0) {
          // key is pressed this frame
          trigger(CoreEvents.KEY_PRESS, null, i);
        }
        // key is still pressed (or pressed the first time)
        trigger(CoreEvents.KEY_DOWN, null, i);
        keysDown[i] = frameNo;
      } else {
        // Key not down, but was down the previous frame? Then it was released this frame.
        if (frame != 0) {
          keysDown[i] = 0;
          trigger(CoreEvents.KEY_UP, null, i);
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
    return random.nextInt(maxExclusive - min) + min;
  }


  /**
   * add scene to game.
   */
  public void scene(String name, Consumer<SceneDescription> sceneCreator) {
    scenes.put(name, sceneCreator);
  }


  /**
   * remove current scene and create new named scene.
   */
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
      public <E extends GameObject> E add(E gameObject, Object... components) {
        return root.add(gameObject, components);
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