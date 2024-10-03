package com.berray.components.core;

import com.berray.GameObject;
import com.berray.math.Matrix4;

import java.util.List;

/** Adds a position vector to the Game Object. */
public abstract class PosComponent<E> extends Component {
  protected E pos;

  public PosComponent(E pos) {
    super("pos");
    this.pos = pos;
  }

  @Override
  public void add(GameObject gameObject) {
    registerBoundProperty("pos", this::getPos, this::setPos);
    registerGetter("posTransform", this::getTransform);
    registerAction("move", this::moveAction, MoveAction::new);
    registerAction("moveBy", this::moveByAction, MoveAction::new);
  }

  /**
   * params:
   * - vec2 velocity
   * - float deltaTime
   */
  public void moveAction(MoveAction<E> params) {
    E velocity = params.getVector();
    float deltaTime = params.getDeltaTime();
    setPos(move(pos, velocity, deltaTime));
  }


  /**
   * params:
   * - vec2 amount
   */
  public void moveByAction(MoveAction<E> params) {
    E amount = params.getVector();
    setPos(move(pos, amount));
  }

  /** Moves the vector <code>pos</code> by amount <code>amount</code>*/
  protected abstract E move(E pos, E amount);

  protected abstract E move(E pos, E amount, float scale);

  protected abstract Matrix4 getTransform();

  public E getPos() {
    return pos;
  }

  public void setPos(E pos) {
    this.pos = pos;
    this.gameObject.setTransformDirty();
  }

  protected static class MoveAction<E> extends Action {
    public MoveAction(List<Object> params) {
      super(params);
    }

    public E getVector() {
      return getParameter(0);
    }
    public float getDeltaTime() {
      return getParameter(1);
    }
  }
}
