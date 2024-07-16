package com.berray;

import com.berray.components.Component;

import java.util.LinkedList;

public class Game {
  private int gravity;
  private int nextGameObjectId = 0;
  private final LinkedList<GameObject> gameObjects;

  // Constructor
  public Game() {
    this.gameObjects = new LinkedList<>();
    init();
  }

  public void init() {
  }

  // Add a game object to the game
  public void add(Component... components) {
    GameObject gameObject = new GameObject(nextGameObjectId++);
    for (Component component : components) {
      component.setId(nextGameObjectId++);
      gameObject.addComponent(component);
    }
    gameObjects.add(gameObject);
  }

  // Update all game objects
  public void update() {
    for (GameObject gameObject : gameObjects) {
      gameObject.update();
    }
  }

  // Draw all game objects
  public void draw() {
    for (GameObject gameObject : gameObjects) {
      gameObject.draw();
    }
  }
}
