package com.berray;

import com.berray.components.Component;

import java.util.LinkedList;

public class Game {
  private int gravity;
  private int nextGameObjectId = 0;
  private final LinkedList<GameObject> gameObjects;



  private int width;
  private int height;

  // Constructor
  public Game() {
    this.gameObjects = new LinkedList<>();
    init();
  }

  public void init() {
  }

  // Add a game object to the game
  public GameObject add(Object... components) {
    GameObject gameObject = new GameObject(nextGameObjectId++);
    gameObject.addComponents(components);
    gameObjects.add(gameObject);
    return gameObject;
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


  public void checkFrame() {
    // checkObj(game.root);
    gameObjects.forEach(this::checkObj);
  }

  public void checkObj(GameObject gameObject) {
    /*
    stack.push(tr.clone());

    // Update object transform here. This will be the transform later used in rendering.
    if (obj.pos) tr.translate(obj.pos);
    if (obj.scale) tr.scale(obj.scale);
    if (obj.angle) tr.rotate(obj.angle);
    obj.transform = tr.clone();

    if (obj.c("area") && !obj.paused) {
      // TODO: only update worldArea if transform changed
                const aobj = obj as GameObj<AreaComp>;
                const area = aobj.worldArea();
                const bbox = area.bbox();

      // Get spatial hash grid coverage
                const xmin = Math.floor(bbox.pos.x / cellSize);
                const ymin = Math.floor(bbox.pos.y / cellSize);
                const xmax = Math.ceil((bbox.pos.x + bbox.width) / cellSize);
                const ymax = Math.ceil((bbox.pos.y + bbox.height) / cellSize);

      // Cache objs that are already checked
                const checked = new Set();

      // insert & check against all covered grids
      for (let x = xmin; x <= xmax; x++) {
        for (let y = ymin; y <= ymax; y++) {
          if (!grid[x]) {
            grid[x] = {};
            grid[x][y] = [aobj];
          } else if (!grid[x][y]) {
            grid[x][y] = [aobj];
          } else {
                            const cell = grid[x][y];
            check: for (const other of cell) {
              if (other.paused) continue;
              if (!other.exists()) continue;
              if (checked.has(other.id)) continue;
              for (const tag of aobj.collisionIgnore) {
                if (other.is(tag)) {
                  continue check;
                }
              }
              for (const tag of other.collisionIgnore) {
                if (aobj.is(tag)) {
                  continue check;
                }
              }
              // TODO: cache the world area here
                                const res = sat(
                  aobj.worldArea(),
                  other.worldArea(),
                  );
              if (res) {
                // TODO: rehash if the object position is changed after resolution?
                                    const col1 = new Collision(
                    aobj,
                    other,
                    res,
                    );
                aobj.trigger("collideUpdate", other, col1);
                                    const col2 = col1.reverse();
                // resolution only has to happen once
                col2.resolved = col1.resolved;
                other.trigger("collideUpdate", aobj, col2);
              }
              checked.add(other.id);
            }
            cell.push(aobj);
          }
        }
      }
    }

    obj.children.forEach(checkObj);
    tr = stack.pop();*/
  }

}
